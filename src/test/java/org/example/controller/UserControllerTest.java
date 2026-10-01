package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.hateoas.UserModelAssembler;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserModelAssembler.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUser() throws Exception {

        UserRequest request = createRequest();

        UserResponse response = createResponse();

        when(userService.createUser(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/users")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Иван")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("ivan@test.com")
                )
                .andExpect(
                        jsonPath("$._links.self.href")
                                .value(
                                        "http://localhost/api/users/1"
                                )
                )
                .andExpect(
                        jsonPath("$._links.users.href")
                                .value(
                                        "http://localhost/api/users"
                                )
                );
    }

    @Test
    void shouldReturnUserById() throws Exception {

        UserResponse response =
                createResponse();

        when(userService.getUserById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Иван")
                )
                .andExpect(
                        jsonPath("$._links.self.href")
                                .value(
                                        "http://localhost/api/users/1"
                                )
                );
    }

    @Test
    void shouldReturnAllUsers()
            throws Exception {

        UserResponse user =
                createResponse();

        when(userService.getAllUsers())
                .thenReturn(
                        List.of(user)
                );

        mockMvc.perform(
                        get("/api/users")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$._embedded.users[0].name"
                        ).value("Иван")
                )
                .andExpect(
                        jsonPath(
                                "$._embedded.users[0]._links.self.href"
                        ).value(
                                "http://localhost/api/users/1"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$._links.self.href"
                        ).value(
                                "http://localhost/api/users"
                        )
                );
    }

    @Test
    void shouldUpdateUser()
            throws Exception {

        UserRequest request =
                createRequest();

        request.setName("Иван Петров");
        request.setAge(26);

        UserResponse response =
                UserResponse.builder()
                        .id(1L)
                        .name("Иван Петров")
                        .email("ivan@test.com")
                        .age(26)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        when(
                userService.updateUser(
                        any(),
                        any()
                )
        ).thenReturn(response);

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.name")
                                .value(
                                        "Иван Петров"
                                )
                )
                .andExpect(
                        jsonPath("$.age")
                                .value(26)
                )
                .andExpect(
                        jsonPath(
                                "$._links.self.href"
                        ).value(
                                "http://localhost/api/users/1"
                        )
                );
    }

    @Test
    void shouldDeleteUser()
            throws Exception {

        doNothing()
                .when(userService)
                .deleteUser(1L);

        mockMvc.perform(
                        delete("/api/users/1")
                )
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank()
            throws Exception {

        UserRequest invalidRequest =
                new UserRequest();

        invalidRequest.setName("");
        invalidRequest.setEmail(
                "ivan@test.com"
        );
        invalidRequest.setAge(25);

        mockMvc.perform(
                        post("/api/users")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        invalidRequest
                                                )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    private UserRequest createRequest() {

        UserRequest request =
                new UserRequest();

        request.setName("Иван");
        request.setEmail(
                "ivan@test.com"
        );
        request.setAge(25);

        return request;
    }

    private UserResponse createResponse() {

        return UserResponse.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@test.com")
                .age(25)
                .createdAt(
                        LocalDateTime.now()
                )
                .build();
    }
}