package com.bash.bank_sampah.restful.controller;

import com.bash.bank_sampah.restful.entity.User;
import com.bash.bank_sampah.restful.model.ContactResponse;
import com.bash.bank_sampah.restful.model.CreateContactRequest;
import com.bash.bank_sampah.restful.model.WebResponse;
import com.bash.bank_sampah.restful.repository.ContactRepository;
import com.bash.bank_sampah.restful.repository.UserRepository;
import com.bash.bank_sampah.restful.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("rhadiyasa");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Rafi Hadiyasa");
        user.setToken("tokenanjing");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
        userRepository.save(user);
    }

    @Test
    void createContactBadRequest() throws Exception {
        CreateContactRequest createContactRequest = new CreateContactRequest();
        createContactRequest.setFirstName("");
        createContactRequest.setEmail("salah");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createContactRequest))
                        .header("X-API-TOKEN", "tokenanjing")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper
                            .readValue(result.getResponse().getContentAsString(),
                                    new TypeReference<WebResponse<String>>() {
                                    });
                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void createContactSuccess() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Rahma");
        request.setLastName("Haifa");
        request.setPhone("08969391924");
        request.setEmail("rafi@gmail.com");


        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "tokenanjing")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<ContactResponse> response = objectMapper
                            .readValue(result.getResponse().getContentAsString(),
                                    new TypeReference<>() {
                                    });
                    assertNull(response.getErrors());
                    assertEquals("Rahma", response.getData().getFirstName());
                    assertEquals("Haifa", response.getData().getLastName());
                    assertEquals("rafi@gmail.com", response.getData().getEmail());
                    assertEquals("08969391924", response.getData().getPhone());

//                    assertTrue(contactRepository.existsById(response.getData().getId()));
                }
        );
    }
}