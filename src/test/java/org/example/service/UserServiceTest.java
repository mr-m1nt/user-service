package org.example.service;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.event.UserEvent;
import org.example.kafka.UserKafkaProducer;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserKafkaProducer kafkaProducer;

    @InjectMocks
    private UserServiceImplements userService;

    private User testUser;
    private UserRequest testRequest;
    private UserResponse testResponse;

    @BeforeEach
    void setUp() {

        testUser =
                User.builder()
                        .id(1L)
                        .name("Иван")
                        .email("ivan@test.com")
                        .age(25)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        testRequest =
                new UserRequest();

        testRequest.setName("Иван");
        testRequest.setEmail(
                "ivan@test.com"
        );
        testRequest.setAge(25);

        testResponse =
                UserResponse.builder()
                        .id(1L)
                        .name("Иван")
                        .email("ivan@test.com")
                        .age(25)
                        .createdAt(
                                testUser.getCreatedAt()
                        )
                        .build();
    }

    @Nested
    @DisplayName("createUser")
    class CreateUserTests {

        @Test
        void shouldCreateUserSuccessfully() {

            when(
                    userRepository.existsByEmail(
                            testRequest.getEmail()
                    )
            ).thenReturn(false);

            when(
                    userMapper.toEntity(
                            testRequest
                    )
            ).thenReturn(testUser);

            when(
                    userRepository.save(
                            any(User.class)
                    )
            ).thenReturn(testUser);

            when(
                    userMapper.toResponse(
                            testUser
                    )
            ).thenReturn(testResponse);

            UserResponse result =
                    userService.createUser(
                            testRequest
                    );

            assertThat(result)
                    .isNotNull();

            assertThat(result.getName())
                    .isEqualTo("Иван");

            assertThat(result.getEmail())
                    .isEqualTo(
                            "ivan@test.com"
                    );

            verify(userRepository)
                    .save(testUser);

            verify(kafkaProducer)
                    .sendEvent(
                            new UserEvent(
                                    "CREATE",
                                    "ivan@test.com"
                            )
                    );
        }

        @Test
        void shouldThrowExceptionWhenEmailExists() {

            when(
                    userRepository.existsByEmail(
                            testRequest.getEmail()
                    )
            ).thenReturn(true);

            assertThatThrownBy(
                    () ->
                            userService.createUser(
                                    testRequest
                            )
            )
                    .isInstanceOf(
                            IllegalArgumentException.class
                    )
                    .hasMessageContaining(
                            "Email уже занят"
                    );

            verify(
                    userRepository,
                    never()
            ).save(any());

            verify(
                    kafkaProducer,
                    never()
            ).sendEvent(any());
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserByIdTests {

        @Test
        void shouldReturnUserWhenFound() {

            when(
                    userRepository.findById(1L)
            ).thenReturn(
                    Optional.of(testUser)
            );

            when(
                    userMapper.toResponse(
                            testUser
                    )
            ).thenReturn(testResponse);

            UserResponse result =
                    userService.getUserById(1L);

            assertThat(result)
                    .isNotNull();

            assertThat(result.getId())
                    .isEqualTo(1L);

            verify(userRepository)
                    .findById(1L);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {

            when(
                    userRepository.findById(
                            999L
                    )
            ).thenReturn(
                    Optional.empty()
            );

            assertThatThrownBy(
                    () ->
                            userService
                                    .getUserById(
                                            999L
                                    )
            )
                    .isInstanceOf(
                            RuntimeException.class
                    )
                    .hasMessageContaining(
                            "Пользователь не найден"
                    );

            verify(
                    userMapper,
                    never()
            ).toResponse(any());
        }
    }

    @Test
    void shouldReturnAllUsers() {

        User user2 =
                User.builder()
                        .id(2L)
                        .name("Мария")
                        .email(
                                "maria@test.com"
                        )
                        .age(28)
                        .build();

        UserResponse response2 =
                UserResponse.builder()
                        .id(2L)
                        .name("Мария")
                        .email(
                                "maria@test.com"
                        )
                        .age(28)
                        .build();

        when(
                userRepository.findAll()
        ).thenReturn(
                List.of(
                        testUser,
                        user2
                )
        );

        when(
                userMapper.toResponse(
                        testUser
                )
        ).thenReturn(testResponse);

        when(
                userMapper.toResponse(
                        user2
                )
        ).thenReturn(response2);

        List<UserResponse> result =
                userService.getAllUsers();

        assertThat(result)
                .hasSize(2);

        assertThat(result)
                .extracting(
                        UserResponse::getName
                )
                .containsExactlyInAnyOrder(
                        "Иван",
                        "Мария"
                );
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {

        when(
                userRepository.findAll()
        ).thenReturn(
                List.of()
        );

        List<UserResponse> result =
                userService.getAllUsers();

        assertThat(result)
                .isEmpty();
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUserTests {

        @Test
        void shouldUpdateUserSuccessfully() {

            UserRequest updateRequest =
                    new UserRequest();

            updateRequest.setName(
                    "Иван Петров"
            );

            updateRequest.setEmail(
                    "ivan@test.com"
            );

            updateRequest.setAge(26);

            User updatedUser =
                    User.builder()
                            .id(1L)
                            .name(
                                    "Иван Петров"
                            )
                            .email(
                                    "ivan@test.com"
                            )
                            .age(26)
                            .build();

            UserResponse updatedResponse =
                    UserResponse.builder()
                            .id(1L)
                            .name(
                                    "Иван Петров"
                            )
                            .email(
                                    "ivan@test.com"
                            )
                            .age(26)
                            .build();

            when(
                    userRepository.findById(
                            1L
                    )
            ).thenReturn(
                    Optional.of(testUser)
            );

            when(
                    userRepository.save(
                            testUser
                    )
            ).thenReturn(
                    updatedUser
            );

            when(
                    userMapper.toResponse(
                            updatedUser
                    )
            ).thenReturn(
                    updatedResponse
            );

            UserResponse result =
                    userService.updateUser(
                            1L,
                            updateRequest
                    );

            assertThat(result.getName())
                    .isEqualTo(
                            "Иван Петров"
                    );

            assertThat(result.getAge())
                    .isEqualTo(26);

            verify(userMapper)
                    .updateEntityFromRequest(
                            updateRequest,
                            testUser
                    );

            verify(userRepository)
                    .save(testUser);
        }

        @Test
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {

            when(
                    userRepository.findById(
                            999L
                    )
            ).thenReturn(
                    Optional.empty()
            );

            assertThatThrownBy(
                    () ->
                            userService.updateUser(
                                    999L,
                                    testRequest
                            )
            )
                    .isInstanceOf(
                            RuntimeException.class
                    )
                    .hasMessageContaining(
                            "Пользователь не найден"
                    );

            verify(
                    userMapper,
                    never()
            ).updateEntityFromRequest(
                    any(),
                    any()
            );

            verify(
                    userRepository,
                    never()
            ).save(any());
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUserTests {

        @Test
        void shouldDeleteExistingUser() {

            when(
                    userRepository.findById(
                            1L
                    )
            ).thenReturn(
                    Optional.of(testUser)
            );

            userService.deleteUser(1L);

            verify(userRepository)
                    .findById(1L);

            verify(userRepository)
                    .deleteById(1L);

            verify(kafkaProducer)
                    .sendEvent(
                            new UserEvent(
                                    "DELETE",
                                    "ivan@test.com"
                            )
                    );
        }

        @Test
        void shouldThrowExceptionWhenDeletingNonExistentUser() {

            when(
                    userRepository.findById(
                            999L
                    )
            ).thenReturn(
                    Optional.empty()
            );

            assertThatThrownBy(
                    () ->
                            userService.deleteUser(
                                    999L
                            )
            )
                    .isInstanceOf(
                            RuntimeException.class
                    )
                    .hasMessageContaining(
                            "Пользователь не найден"
                    );

            verify(
                    userRepository,
                    never()
            ).deleteById(
                    any()
            );

            verify(
                    kafkaProducer,
                    never()
            ).sendEvent(
                    any()
            );
        }
    }
}