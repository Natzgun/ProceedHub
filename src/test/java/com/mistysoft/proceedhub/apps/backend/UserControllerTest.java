package com.mistysoft.proceedhub.apps.backend;

import com.mistysoft.proceedhub.modules.user.infrastructure.security.JwtUtil;
import com.mistysoft.proceedhub.modules.user.domain.*;
import com.mistysoft.proceedhub.modules.user.infrastructure.ISpringDataUserRepository;
import com.mistysoft.proceedhub.modules.user.infrastructure.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.Cookie;
import java.util.Set;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ISpringDataUserRepository users;
    @Autowired JwtUtil jwt;

    private static RequestPostProcessor csrfCookie() {
        return request -> {
            Cookie[] existing = request.getCookies();
            Cookie[] cookies = java.util.Arrays.copyOf(existing == null ? new Cookie[0] : existing,
                    (existing == null ? 0 : existing.length) + 1);
            cookies[cookies.length - 1] = new Cookie("XSRF-TOKEN", "test-csrf-token");
            request.setCookies(cookies);
            request.addHeader("X-XSRF-TOKEN", "test-csrf-token");
            return request;
        };
    }

    @BeforeEach
    void reset() {
        users.deleteAll();
    }

    @Test
    void registrationDoesNotAcceptClientProvidedAdminRole() throws Exception {
        mvc.perform(post("/api/users/register").with(csrfCookie()).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","email":"alice@example.com","password":"secret","roles":["ADMIN"]}
                                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.roles[0]").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(post("/api/users/register").with(csrfCookie()).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"bob","email":"alice@example.com","password":"secret"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void cookieAuthenticationEnforcesRolesAndOwnership() throws Exception {
        users.save(UserMapper.toEntity(User.restore(new UserId("1"), "admin", "admin@example.com",
                "hash", Set.of(Role.ADMIN))));
        users.save(UserMapper.toEntity(User.register(new UserId("2"), "alice", "alice@example.com", "hash")));
        String body = """
                {"title":"Scholarship"}
                """;
        mvc.perform(post("/api/scholarships/create").with(csrfCookie()).contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/scholarships/create").with(csrfCookie()).cookie(new Cookie("token", jwt.generateToken("alice")))
                        .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
        String fullBody = """
                {"title":"Scholarship","description":"For students","image":"https://example.com/a.png",
                 "country":"Peru","continent":"South America","moreInfo":"https://example.com","requirements":[]}
                """;
        Cookie adminToken = new Cookie("token", jwt.generateToken("admin"));
        mvc.perform(post("/api/scholarships/create").cookie(adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(fullBody)).andExpect(status().isForbidden());
        mvc.perform(post("/api/scholarships/create").with(csrfCookie()).cookie(adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(fullBody)).andExpect(status().isCreated());
        mvc.perform(get("/api/users/admin").cookie(new Cookie("token", jwt.generateToken("alice"))))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/users/verifyToken").cookie(new Cookie("token", jwt.generateToken("alice"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("alice"));
        mvc.perform(get("/api/users/verifyToken").cookie(new Cookie("token", "invalid")))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/users/verifyToken")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginSetsHttpOnlyCookieAndMutationsRequireCsrf() throws Exception {
        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"alice","email":"alice@example.com","password":"secret"}
                        """))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/users/csrf")).andExpect(status().isOk()).andExpect(jsonPath("$.token").exists());
        mvc.perform(post("/api/users/register").with(csrfCookie()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"alice","email":"alice@example.com","password":"secret"}
                        """))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/users/login").with(csrfCookie()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"alice","password":"secret"}
                        """))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=3600")));
        mvc.perform(post("/api/users/login").with(csrfCookie()).secure(true).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"alice","password":"secret"}
                        """))
                .andExpect(status().isOk()).andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Secure")));
        mvc.perform(post("/api/users/login").with(csrfCookie()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"alice","password":"wrong"}
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void csrfCookieAndHeaderAllowBrowserRegistration() throws Exception {
        var csrfResponse = mvc.perform(get("/api/users/csrf")).andExpect(status().isOk()).andReturn().getResponse();
        String token = com.jayway.jsonpath.JsonPath.read(csrfResponse.getContentAsString(), "$.token");
        org.junit.jupiter.api.Assertions.assertTrue(csrfResponse.getHeaders("Set-Cookie").stream()
                .anyMatch(cookie -> cookie.startsWith("XSRF-TOKEN=" + token + ";")));
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", token);
        mvc.perform(post("/api/users/register").cookie(csrfCookie).header("X-XSRF-TOKEN", token)
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"alice","email":"alice@example.com","password":"secret"}
                        """))
                .andExpect(status().isCreated());
    }
}
