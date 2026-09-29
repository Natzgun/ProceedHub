package com.mistysoft.proceedhub.apps.backend;

import com.mistysoft.proceedhub.apps.backend.dto.LoginUserRequest;
import com.mistysoft.proceedhub.apps.backend.dto.RegisterUserRequest;
import com.mistysoft.proceedhub.apps.backend.dto.UserResponse;
import com.mistysoft.proceedhub.modules.user.application.TokenIssuer;
import com.mistysoft.proceedhub.modules.user.application.LoginUser;
import com.mistysoft.proceedhub.modules.user.application.RegisterUser;
import com.mistysoft.proceedhub.modules.user.application.SearchUser;
import com.mistysoft.proceedhub.modules.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RegisterUser registerUser;
    private final LoginUser loginUser;
    private final SearchUser searchUser;
    private final TokenIssuer tokenIssuer;

    public UserController(RegisterUser registerUser, LoginUser loginUser, SearchUser searchUser,
                          TokenIssuer tokenIssuer) {
        this.registerUser = registerUser;
        this.loginUser = loginUser;
        this.searchUser = searchUser;
        this.tokenIssuer = tokenIssuer;
    }

    @GetMapping("/csrf")
    public java.util.Map<String, String> csrf(CsrfToken token, HttpServletRequest request,
                                              HttpServletResponse response) {
        String value = token.getToken();
        if (response.getHeaders(HttpHeaders.SET_COOKIE).stream()
                .noneMatch(cookie -> cookie.startsWith("XSRF-TOKEN="))) {
            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("XSRF-TOKEN", value)
                    .path("/").secure(request.isSecure()).sameSite("Lax").build().toString());
        }
        return java.util.Map.of("token", value);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody RegisterUserRequest request) {
        User user = registerUser.execute(request.username(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@RequestBody LoginUserRequest request, HttpServletRequest servletRequest) {
        User user;
        try {
            user = loginUser.execute(request.username(), request.password());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        TokenIssuer.IssuedToken token = tokenIssuer.issue(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("token", token.value())
                .httpOnly(true).secure(servletRequest.isSecure()).sameSite("Lax")
                .path("/").maxAge(token.maxAgeSeconds()).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(UserResponse.from(user));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username, Authentication authentication) {
        if (!username.equals(authentication.getName()) && authentication.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return searchUser.findByUsername(username).map(UserResponse::from)
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/verifyToken")
    public ResponseEntity<UserResponse> verifyToken(Authentication authentication) {
        return searchUser.findByUsername(authentication.getName()).map(UserResponse::from)
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
