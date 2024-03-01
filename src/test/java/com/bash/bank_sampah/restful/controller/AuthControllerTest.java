package com.bash.bank_sampah.restful.controller;

import com.bash.bank_sampah.restful.entity.User;
import com.bash.bank_sampah.restful.model.WebResponse;
import com.bash.bank_sampah.restful.repository.UserRepository;
import com.bash.bank_sampah.restful.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

//    @BeforeEach
//    void setUp() {
//        userRepository.deleteAll();
//    }

    @Test
    void logoutFailed() throws Exception {
        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<>() {
                            }
                    );
                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void logoutSuccess() throws Exception {

        User user = new User();
        user.setUsername("testlagi");
        user.setName("namalagi");
        user.setPassword(BCrypt.hashpw("passwordlagi", BCrypt.gensalt()));
        user.setToken("testlagi");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000000000L);
        userRepository.save(user);


        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testlagi")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<>() {
                            }
                    );
                    assertNull(response.getErrors());
                    assertEquals("OK", response.getData());

                    User userDb = userRepository.findById("testlagi").orElse(null);
                    assertNotNull(userDb);
                    assertNull(userDb.getTokenExpiredAt());
                    assertNull(userDb.getToken());
                }
        );
    }

}