# Local Kubernetes deployment

This directory deploys ProceedHub, HamukSpring, and PostgreSQL 16.15 on a local Kubernetes cluster. PostgreSQL runs in a StatefulSet backed by a PersistentVolumeClaim (PVC) provisioned by the cluster. It does not reuse or modify the Docker Compose volume `databases_postgres_data`.

## 1. Build the images

The local setup uses a kind cluster named `proceedhub`. Install kind once and create the cluster only if it does not already exist:

```sh
go install sigs.k8s.io/kind@v0.32.0
"$HOME/go/bin/kind" create cluster --name proceedhub
kubectl config current-context
```

From the backend repository root, build both images and import them into the kind node. Set `FRONTEND_DIR` to the absolute path of your frontend clone:

```sh
FRONTEND_DIR="/path/to/HamukSpring"
docker build -t proceedhub-backend:dev .
docker build -t proceedhub-frontend:dev "$FRONTEND_DIR"
"$HOME/go/bin/kind" load docker-image proceedhub-backend:dev proceedhub-frontend:dev --name proceedhub
```

The Deployments deliberately use `imagePullPolicy: Never`; they will not attempt to pull local images from an inaccessible registry. Re-import images after rebuilding them. For remote clusters, publish versioned images and change their references and pull policies.

The frontend Dockerfile builds with `VITE_API_URL=/api`. Nginx forwards `/api/` to the backend Service, so the browser, JWT cookie, and CSRF cookie share the frontend's origin. This is why exposing the backend through a second local browser port is unnecessary.

## 2. Create credentials outside the repositories

Keep the secret values in `$HOME/.config/proceedhub/k8s.env`, **outside both Git repositories**. Create the parent directory with restricted access:

```sh
mkdir -p -m 700 "$HOME/.config/proceedhub"
chmod 700 "$HOME/.config/proceedhub"
```

In your editor, create `$HOME/.config/proceedhub/k8s.env` with three `KEY=value` entries: `DB_USERNAME`, `DB_PASSWORD`, and `JWT_TOKEN`. Then protect the file:

```sh
chmod 600 "$HOME/.config/proceedhub/k8s.env"
```

Use a strong random JWT secret (for example, generate one locally with `openssl rand -hex 32`). Do not put credentials into frontend `VITE_*` variables: their values end up in browser JavaScript. The non-secret `PORT`, `DB_URL`, `JWT_EXPIRATION`, `POSTGRES_DB`, and `CORS_ALLOWED_ORIGINS` settings live in the versioned `k8s/kustomization.yaml` ConfigMap generator.

After selecting the intended cluster with `kubectl config current-context`, create the Namespace and then the Secret directly from your local file:

```sh
kubectl apply -f k8s/namespace.yaml
kubectl -n proceedhub create secret generic proceedhub-secrets \
  --from-env-file="$HOME/.config/proceedhub/k8s.env" \
  --dry-run=client -o yaml | kubectl apply -f -
```

No Secret manifest or credentials file is stored in this repository. Kubernetes Secrets are base64-encoded, **not encrypted by default**: restrict cluster access and never commit a generated Secret YAML or a database dump. Updating `DB_PASSWORD` in the file/Secret does **not** change an already initialized database password; password rotation needs a coordinated database change. After changing `JWT_TOKEN`, restart the backend Deployment to load the new environment variable.

## 3. Deploy and inspect

From the backend repository root, install Metrics Server as described in section 4 before applying the application manifests (otherwise HPA will report unknown CPU metrics):

```sh
kubectl apply -k k8s/
kubectl -n proceedhub get pods,services,pvc
kubectl -n proceedhub rollout status statefulset/postgres
kubectl -n proceedhub rollout status deployment/backend
kubectl -n proceedhub rollout status deployment/frontend
kubectl -n proceedhub port-forward service/frontend 8080:80
```

Visit `http://localhost:8080`. Keep the port-forward terminal running. If the PVC stays `Pending`, inspect `kubectl get storageclass`: this setup needs a default StorageClass capable of provisioning 5 GiB. For startup diagnostics use `kubectl -n proceedhub describe pod postgres-0` and `kubectl -n proceedhub logs deployment/backend`. If you update the local Secret after deployment, restart the affected Pods to refresh environment variables, for example `kubectl -n proceedhub rollout restart deployment/backend` for a JWT change.

The PostgreSQL Service is internal to the cluster, and the frontend Service is accessed locally with port-forward. Kubernetes schedules the PostgreSQL Pod and provisions its PVC `postgres-data-postgres-0`; the StorageClass manages the actual storage. The StatefulSet explicitly retains its PVC on deletion or scale-down. **A PVC is not a backup**: the local kind node stores the provisioned volume, so deleting the kind cluster can destroy it even though the StatefulSet retained the PVC. Keep a separate database backup outside both repositories, and do not delete the PVC while its data matters.

### Start again after a machine reboot

Start Docker, then check whether the existing kind node is running with `docker ps -a --filter name=proceedhub-control-plane`. If it is stopped, start that **existing** node (do not create a new cluster):

```sh
docker start proceedhub-control-plane
kubectl config use-context kind-proceedhub
kubectl wait --for=condition=Ready node/proceedhub-control-plane --timeout=120s
kubectl -n proceedhub rollout status statefulset/postgres --timeout=180s
kubectl -n proceedhub rollout status deployment/backend --timeout=180s
kubectl -n proceedhub rollout status deployment/frontend --timeout=180s
kubectl -n proceedhub port-forward service/frontend 8080:80
```

Keep the final command running in its own terminal and visit `http://localhost:8080`. Kubernetes controllers restart the existing Pods; rebuilding images, recreating credentials, reapplying manifests, and restoring the database are not needed after a normal reboot. Deleting/recreating the kind cluster is a different operation and can destroy the database volume.

## 4. Replicas

`hpa.yaml` manages backend and frontend replica counts. Each HPA keeps at least 2 Pods and allows up to 4 per Deployment, targeting 70% average CPU utilization relative to the CPU requests in `backend.yaml` (100m) and `frontend.yaml` (50m). Scale-up is limited to 2 Pods per minute; scale-down waits 5 minutes and removes at most 1 Pod per minute to avoid oscillation. CPU is a starting signal, not a measure of HTTP request latency: adjust requests, targets, and maximums using load tests and observed capacity. The Deployment manifests omit `spec.replicas` so reapplying them does not override the HPA. Do not use `kubectl scale` to maintain desired replicas while HPA is enabled.

HPA requires the Metrics API. For this local kind cluster, install the pinned Metrics Server release; kind's kubelet serving certificate is not trusted by Metrics Server, so disable **only kubelet certificate validation in this local test cluster**:

```sh
kubectl config current-context # expect kind-proceedhub
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.9.0/components.yaml
kubectl -n kube-system patch deployment metrics-server --type=json \
  -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
kubectl -n kube-system rollout status deployment/metrics-server --timeout=180s
kubectl top nodes
kubectl -n proceedhub top pods
```

On an existing cluster where these Deployments were previously applied with `spec.replicas: 2`, run `kubectl -n proceedhub apply set-last-applied -f k8s/backend.yaml` and `kubectl -n proceedhub apply set-last-applied -f k8s/frontend.yaml` **once before** `kubectl apply -k k8s/`. This updates the client-side apply annotation without changing the live replica counts, preventing a temporary scale-down to the Deployment default of one Pod. On a fresh cluster, install Metrics Server before applying the application manifests.

```sh
kubectl apply -k k8s/
kubectl -n proceedhub get hpa,deployments
kubectl -n proceedhub describe hpa backend
```

Wait for `TARGETS` in `get hpa` to show a CPU percentage rather than `<unknown>`; the metrics pipeline needs time to collect samples. PostgreSQL is deliberately **not** autoscaled. HPA scales Pods, not Kubernetes nodes: in this one-node kind cluster all replicas still run on `proceedhub-control-plane`, whose default configuration allows application workloads. To test worker-node placement, create a separate multi-node kind cluster with worker nodes and import the local images there; do not delete or recreate this cluster while its PostgreSQL PVC contains data. Production clusters commonly isolate the control plane with a taint and use separate worker nodes; node autoscaling, if required, is a separate mechanism.

PostgreSQL deliberately remains at one replica: increasing `spec.replicas` on this StatefulSet would start a second independent PostgreSQL instance with a separate PVC, **not** a synchronized database standby. Database high availability requires PostgreSQL replication and failover orchestration (for example, an operator). This local kind cluster has only one node, so two application Pods demonstrate process redundancy but do not protect against loss of that node.

## 5. Optional: copy the existing ProceedHub data

The Kubernetes database initially starts empty. To migrate the existing `proceedhub` database, first ensure the old Compose PostgreSQL is running. Set `COMPOSE_FILE` to its Docker Compose file, save a backup **outside both repositories**, and keep the backend stopped while restoring. Remove the backend HPA before scaling to zero, or it would immediately start new backend Pods during the restore:

```sh
COMPOSE_FILE="/path/to/DATABASES/docker-compose.yml"
docker compose -f "$COMPOSE_FILE" exec -T db sh -c 'pg_dump -U "$POSTGRES_USER" -d proceedhub -Fc' > "$HOME/.config/proceedhub/proceedhub-before-k8s.dump"
kubectl -n proceedhub delete hpa backend --ignore-not-found
kubectl -n proceedhub scale deployment/backend --replicas=0
kubectl -n proceedhub rollout status statefulset/postgres
kubectl -n proceedhub exec -i postgres-0 -- sh -c 'pg_restore -U "$POSTGRES_USER" -d "$POSTGRES_DB" --no-owner --no-acl --clean --if-exists --exit-on-error' < "$HOME/.config/proceedhub/proceedhub-before-k8s.dump"
kubectl -n proceedhub scale deployment/backend --replicas=2
kubectl -n proceedhub rollout status deployment/backend
kubectl -n proceedhub apply -f k8s/hpa.yaml
```

Check that the backup was created successfully before restoring. On this machine the initial migration preserved 2 users and 8 scholarships; the original Compose container was returned to its stopped state. This procedure copies only the `proceedhub` database; it does not modify the original Compose volume or its other databases. Keep the dump private as a backup. The database is initialized from `POSTGRES_DB=proceedhub` in `kustomization.yaml`; database credentials in your local `k8s.env` may differ from the Compose credentials because the restore uses `--no-owner`.

## Where files belong

- **Backend repository:** version the `k8s/` manifests, this guide, and `.dockerignore`. Never version `$HOME/.config/proceedhub/k8s.env`, a generated Secret YAML, or the existing backend `docker-compose.yml`, which contains plaintext credentials.
- **Frontend repository:** version its `Dockerfile`, `nginx.conf`, `.dockerignore`, and `.gitignore`. A Vite `VITE_*` variable is embedded into browser JavaScript at build time, so **never put secrets in it**.
- **Existing database Compose directory:** retain its `.env` and named volume for local development or backup. It is not a Kubernetes PVC and is not mounted into this cluster.

This is a single-instance local deployment: Kubernetes manages the PostgreSQL container and PVC, but it does not provide database replication or backups automatically. For a team/production cluster, use a PostgreSQL operator with a backup/restore procedure and an external secret manager or encrypted GitOps secrets. Replace the `:dev` images with registry images and configure an externally reachable frontend with TLS.
