package org.example.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для UserService.
 * НЕ используют @SpringBootTest — это чистые unit-тесты с Mockito.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequest testRequest;
    private UserResponse testResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@test.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = new UserRequest();
        testRequest.setName("Иван");
        testRequest.setEmail("ivan@test.com");
        testRequest.setAge(25);

        testResponse = UserResponse.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@test.com")
                .age(25)
                .createdAt(testUser.getCreatedAt())
                .build();
    }

    // ===== Тесты createUser =====

    @Nested
    @DisplayName("createUser")
    class CreateUserTests {

        @Test
        @DisplayName("Должен создать пользователя с валидными данными")
        void shouldCreateUserSuccessfully() {
            // given
            when(userRepository.existsByEmail(testRequest.getEmail())).thenReturn(false);
            when(userMapper.toEntity(testRequest)).thenReturn(testUser);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toResponse(testUser)).thenReturn(testResponse);

            // when
            UserResponse result = userService.createUser(testRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Иван");
            assertThat(result.getEmail()).isEqualTo("ivan@test.com");

            verify(userRepository).existsByEmail(testRequest.getEmail());
            verify(userMapper).toEntity(testRequest);
            verify(userRepository).save(any(User.class));
            verify(userMapper).toResponse(testUser);
        }

        @Test
        @DisplayName("Должен выбросить исключение, если email уже занят")
        void shouldThrowExceptionWhenEmailExists() {
            // given
            when(userRepository.existsByEmail(testRequest.getEmail())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.createUser(testRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email уже занят");

            // Проверяем, что save НЕ вызывался
            verify(userRepository, never()).save(any());
            verify(userMapper, never()).toEntity(any());
        }
    }

    // ===== Тесты getUserById =====

    @Nested
    @DisplayName("getUserById")
    class GetUserByIdTests {

        @Test
        @DisplayName("Должен вернуть пользователя по ID")
        void shouldReturnUserWhenFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userMapper.toResponse(testUser)).thenReturn(testResponse);

            // when
            UserResponse result = userService.getUserById(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Должен выбросить исключение, если пользователь не найден")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(userMapper, never()).toResponse(any());
        }
    }

    // ===== Тесты getAllUsers =====

    @Test
    @DisplayName("getAllUsers должен вернуть список всех пользователей")
    void shouldReturnAllUsers() {
        // given
        User user2 = User.builder().id(2L).name("Мария").email("maria@test.com").age(28).build();
        UserResponse response2 = UserResponse.builder().id(2L).name("Мария").email("maria@test.com").age(28).build();

        when(userRepository.findAll()).thenReturn(List.of(testUser, user2));
        when(userMapper.toResponse(testUser)).thenReturn(testResponse);
        when(userMapper.toResponse(user2)).thenReturn(response2);

        // when
        List<UserResponse> result = userService.getAllUsers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserResponse::getName)
                .containsExactlyInAnyOrder("Иван", "Мария");
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("getAllUsers должен вернуть пустой список, если пользователей нет")
    void shouldReturnEmptyListWhenNoUsers() {
        // given
        when(userRepository.findAll()).thenReturn(List.of());

        // when
        List<UserResponse> result = userService.getAllUsers();

        // then
        assertThat(result).isEmpty();
    }

    // ===== Тесты updateUser =====

    @Nested
    @DisplayName("updateUser")
    class UpdateUserTests {

        @Test
        @DisplayName("Должен обновить существующего пользователя")
        void shouldUpdateUserSuccessfully() {
            // given
            UserRequest updateRequest = new UserRequest();
            updateRequest.setName("Иван Петров");
            updateRequest.setEmail("ivan@test.com");
            updateRequest.setAge(26);

            User updatedUser = User.builder()
                    .id(1L).name("Иван Петров").email("ivan@test.com").age(26).build();
            UserResponse updatedResponse = UserResponse.builder()
                    .id(1L).name("Иван Петров").email("ivan@test.com").age(26).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            doNothing().when(userMapper).updateEntityFromRequest(updateRequest, testUser);
            when(userRepository.save(testUser)).thenReturn(updatedUser);
            when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

            // when
            UserResponse result = userService.updateUser(1L, updateRequest);

            // then
            assertThat(result.getName()).isEqualTo("Иван Петров");
            assertThat(result.getAge()).isEqualTo(26);

            verify(userMapper).updateEntityFromRequest(updateRequest, testUser);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Должен выбросить исключение при обновлении несуществующего пользователя")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updateUser(999L, testRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(userMapper, never()).updateEntityFromRequest(any(), any());
            verify(userRepository, never()).save(any());
        }
    }

    // ===== Тесты deleteUser =====

    @Nested
    @DisplayName("deleteUser")
    class DeleteUserTests {

        @Test
        @DisplayName("Должен удалить существующего пользователя")
        void shouldDeleteExistingUser() {
            // given
            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);

            // when
            userService.deleteUser(1L);

            // then
            verify(userRepository).existsById(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Должен выбросить исключение при удалении несуществующего пользователя")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            // given
            when(userRepository.existsById(999L)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(userRepository, never()).deleteById(anyLong());
        }
    }
}
