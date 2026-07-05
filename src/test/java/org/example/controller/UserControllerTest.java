package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)  // Загружает ТОЛЬКО веб-слой
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean  // Подменяет реальный Service на mock
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;  // Для сериализации JSON

    @Test
    void shouldCreateUser() throws Exception {
        // given
        UserRequest request = new UserRequest();
        request.setName("Иван");
        request.setEmail("ivan@test.com");
        request.setAge(25);

        UserResponse response = UserResponse.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@test.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.createUser(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@test.com"));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        // given
        UserResponse user = UserResponse.builder()
                .id(1L).name("Иван").email("ivan@test.com")
                .age(25).createdAt(LocalDateTime.now()).build();

        when(userService.getAllUsers()).thenReturn(List.of(user));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Иван"));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        // given — невалидный запрос (пустое имя)
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setName("");
        invalidRequest.setEmail("ivan@test.com");
        invalidRequest.setAge(25);

        // when & then — ожидаем 400 Bad Request
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
