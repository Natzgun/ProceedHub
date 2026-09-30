package com.mistysoft.proceedhub.apps.backend;

import com.mistysoft.proceedhub.modules.scholarship.infrastructure.ISpringDataScholarshipRepository;
import com.mistysoft.proceedhub.modules.user.domain.*;
import com.mistysoft.proceedhub.modules.user.infrastructure.ISpringDataUserRepository;
import com.mistysoft.proceedhub.modules.user.infrastructure.UserMapper;
import com.mistysoft.proceedhub.modules.user.infrastructure.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ScholarshipControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ISpringDataUserRepository users;
    @Autowired ISpringDataScholarshipRepository scholarships;
    @Autowired JwtUtil jwt;

    @BeforeEach
    void seedAdmin() {
        scholarships.deleteAll();
        users.deleteAll();
        users.save(UserMapper.toEntity(User.restore(new UserId("admin-id"), "admin",
                "admin@example.com", "hash", Set.of(Role.ADMIN))));
    }

    private RequestPostProcessor adminRequest() {
        return request -> {
            request.setCookies(new Cookie("token", jwt.generateToken("admin")),
                    new Cookie("XSRF-TOKEN", "test-token"));
            request.addHeader("X-XSRF-TOKEN", "test-token");
            return request;
        };
    }

    @Test
    void managesScholarshipsThroughHttpWithoutExposingTheDomain() throws Exception {
        String payload = """
                {"title":"Research grant","description":"For students","date":"2020-01-01T00:00:00Z",
                 "image":"https://example.com/image.png","country":"Peru","continent":"South America",
                 "moreInfo":"https://example.com","requirements":[{"name":"Proof of enrollment"}]}
                """;
        var createResponse = mvc.perform(post("/api/scholarships/create").with(adminRequest())
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Research grant"))
                .andExpect(jsonPath("$.requirements[0].name").value("Proof of enrollment"))
                .andReturn().getResponse();
        String id = com.jayway.jsonpath.JsonPath.read(createResponse.getContentAsString(), "$.id");
        org.junit.jupiter.api.Assertions.assertFalse(createResponse.getContentAsString()
                .contains("2020-01-01"));

        mvc.perform(get("/api/scholarships/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Research grant"))
                .andExpect(jsonPath("$.requirements[0].name").value("Proof of enrollment"));
        mvc.perform(get("/api/scholarships/get_all")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        mvc.perform(post("/api/scholarships/update/{id}", id).with(adminRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated grant","requirements":[]}
                                """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Updated grant"))
                .andExpect(jsonPath("$.description").value("For students"))
                .andExpect(jsonPath("$.requirements").isEmpty());
        mvc.perform(get("/api/scholarships/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated grant"))
                .andExpect(jsonPath("$.requirements").isEmpty());

        mvc.perform(delete("/api/scholarships/delete/{id}", id).with(adminRequest()))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/scholarships/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidOrMissingScholarshipsWithMeaningfulStatus() throws Exception {
        mvc.perform(post("/api/scholarships/create").with(adminRequest())
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title":" "}
                                """))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/scholarships/create").with(adminRequest())
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title":"Grant","requirements":[{"name":" "}]}
                                """))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/scholarships/missing")).andExpect(status().isNotFound());
        mvc.perform(post("/api/scholarships/update/missing").with(adminRequest())
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title":"Updated"}
                                """))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/scholarships/delete/missing").with(adminRequest()))
                .andExpect(status().isNotFound());
    }
}
