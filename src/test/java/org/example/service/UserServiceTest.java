package org.example.service;

import org.example.Dao.UserDao;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImplements userService;
    private User testUser;
    @BeforeEach
    void setUp() {
        testUser = new User("Ilya", "ilya@mail.com", 30);
        testUser.setId(1L);
    }
    @Nested
    @DisplayName("createUser test")
    class CreateUserTests {
        @Test
        @DisplayName("Must create a user with valid data")
        void shouldCreateUserWithValidData() {
            doNothing().when(userDao).save(any(User.class));
            User result = userService.createUser("Sam", "sam@mail.com", 31);
            assertNotNull(result);
            assertEquals("Sam", result.getName());
            assertEquals("sam@mail.com", result.getEmail());
            assertEquals(31, result.getAge());
            verify(userDao, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw an exception if the name is empty.")
        void shouldThrowExceptionWhenNameIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser("", "test@mail.com", 32)
            );
            assertTrue(exception.getMessage().contains("The name cannot be empty"));
            verify(userDao, never()).save(any());
        }

        @Test
        @DisplayName("Should throw an exception on email without @")
        void shouldThrowExceptionWhenEmailInvalid() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser("Max", "email", 33)
            );
            assertTrue(exception.getMessage().contains("@"));
            verify(userDao, never()).save(any());
        }

        @Test
        @DisplayName("Should throw an exception if age < 0")
        void shouldThrowExceptionWhenAgeIsNegative() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser("John", "john@mail.com", -1)
            );
            assertTrue(exception.getMessage().contains("Age"));
            verify(userDao, never()).save(any());
        }

        @Test
        @DisplayName("Should throw an exception if age > 150")
        void shouldThrowExceptionWhenAgeTooLarge() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser("Alice", "alice@test.com", 101)
            );
            verify(userDao, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getUserById test")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return the user if found")
        void shouldReturnUserWhenFound() {
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            Optional<User> result = userService.getUserById(1L);
            assertTrue(result.isPresent());
            assertEquals("Ilya", result.get().getName());
            verify(userDao, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return an empty Optional if the user is not found.")
        void shouldReturnEmptyWhenNotFound() {
            when(userDao.findById(999L)).thenReturn(Optional.empty());
            Optional<User> result = userService.getUserById(999L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw an exception if ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> userService.getUserById(null));
            verify(userDao, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw an exception if the ID is negative.")
        void shouldThrowExceptionWhenIdIsNegative() {
            assertThrows(IllegalArgumentException.class,
                    () -> userService.getUserById(-1L));
            verify(userDao, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("updateUser test")
    class UpdateUserTests {

        @Test
        @DisplayName("Must update username")
        void shouldUpdateUserName() {
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            doNothing().when(userDao).update(any(User.class));
            User result = userService.updateUser(1L, "Antony", null, null);
            assertEquals("Antony", result.getName());
            assertEquals("ilya@mail.com", result.getEmail());
            verify(userDao, times(1)).update(any(User.class));
        }

        @Test
        @DisplayName("Should throw an exception if the user is not found.")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userDao.findById(999L)).thenReturn(Optional.empty());
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> userService.updateUser(999L, "New", null, null)
            );
            assertTrue(exception.getMessage().contains("not found"));
            verify(userDao, never()).update(any());
        }
    }

    @Nested
    @DisplayName("deleteUser test")
    class DeleteUserTests {
        @Test
        @DisplayName("Should delete the existing user and return true")
        void shouldDeleteExistingUser() {
            when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
            doNothing().when(userDao).delete(1L);
            boolean result = userService.deleteUser(1L);
            assertTrue(result);
            verify(userDao, times(1)).delete(1L);
        }

        @Test
        @DisplayName("Should return false if the user is not found.")
        void shouldReturnFalseWhenUserNotFound() {
            when(userDao.findById(999L)).thenReturn(Optional.empty());
            boolean result = userService.deleteUser(999L);
            assertFalse(result);
            verify(userDao, never()).delete(anyLong());
        }
    }

    @Test
    @DisplayName("Should return a list of all users")
    void shouldReturnAllUsers() {
        User user2 = new User("Mary", "mary@mail.com", 34);
        when(userDao.findAll()).thenReturn(List.of(testUser, user2));
        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }
}