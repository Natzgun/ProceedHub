FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew build -x test --no-daemon

FROM eclipse-temurin:25-jre
LABEL authors="natzgun"
WORKDIR /app
COPY --from=builder /app/build/libs/ProceedHub-0.0.1-SNAPSHOT.jar /app/ProceedHub-0.0.1-SNAPSHOT.jar

EXPOSE 8086

CMD ["java", "-jar", "ProceedHub-0.0.1-SNAPSHOT.jar"]
