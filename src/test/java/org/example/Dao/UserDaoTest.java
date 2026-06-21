package org.example.Dao;

import org.example.BaseIntegrationTest;
import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest extends BaseIntegrationTest {
    private UserDaoImplements userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImplements(sessionFactory);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @DisplayName("Must save the user in the database")
    void shouldSaveUserToDatabase() {
        User user = new User("Ilya", "ilya@mail.com", 30);
        userDao.save(user);
        Optional<User> found = userDao.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("Ilya", found.get().getName());
        assertEquals("ilya@mail.com", found.get().getEmail());
        assertEquals(30, found.get().getAge());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    @DisplayName("Must generate ID after saving")
    void shouldGenerateIdAfterSave() {
        User user = new User("Sam", "sam@mail.com", 31);
        assertNull(user.getId()); // до сохранения ID = null
        userDao.save(user);
        assertNotNull(user.getId());
        assertTrue(user.getId() > 0);
    }

    @Test
    @DisplayName("Should automatically set created_at")
    void shouldSetCreatedAtAutomatically() {
        User user = new User("Mary", "mery@mail.com", 32);
        userDao.save(user);
        Optional<User> found = userDao.findById(user.getId());
        assertTrue(found.isPresent());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    @DisplayName("Should return user by ID")
    void shouldFindUserById() {
        User user = new User("Ilya", "ilya@mail.com", 30);
        userDao.save(user);
        Optional<User> found = userDao.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("ilya@mail.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should return an empty Optional if the user is not found.")
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> found = userDao.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should return all users")
    void shouldFindAllUsers() {
        userDao.save(new User("Ilya", "ilya@mail.com", 30));
        userDao.save(new User("Sam", "sam@mail.com", 31));
        userDao.save(new User("Mary", "mary@mail.com", 32));
        List<User> users = userDao.findAll();
        assertEquals(3, users.size());
        List<String> names = users.stream()
                .map(User::getName)
                .toList();
        assertTrue(names.contains("Ilya"));
        assertTrue(names.contains("Sam"));
        assertTrue(names.contains("Mary"));
    }

    @Test
    @DisplayName("Should return an empty list if there are no users.")
    void shouldReturnEmptyListWhenNoUsers() {
        List<User> users = userDao.findAll();
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Must update user data")
    void shouldUpdateUser() {
        User user = new User("Ilya", "ilya@mail.com", 30);
        userDao.save(user);
        User toUpdate = userDao.findById(user.getId()).orElseThrow();
        toUpdate.setName("John");
        toUpdate.setAge(29);
        userDao.update(toUpdate);
        Optional<User> found = userDao.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
        assertEquals(29, found.get().getAge());
        assertEquals("ilya@mail.com", found.get().getEmail()); // не изменился
    }

    @Test
    @DisplayName("Must delete user")
    void shouldDeleteUser() {
        User user = new User("Ilya", "ilya@mail.com", 30);
        userDao.save(user);
        Long id = user.getId();
        userDao.delete(id);
        Optional<User> found = userDao.findById(id);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Shouldn't crash when deleting a non-existent user.")
    void shouldNotFailWhenDeletingNonExistentUser() {
        assertDoesNotThrow(() -> userDao.delete(999L));
    }

    @Test
    @DisplayName("Should throw an exception when email is duplicated")
    void shouldThrowExceptionWhenEmailIsDuplicate() {
        userDao.save(new User("Ilya", "ilya@mail.com", 30));
        User duplicate = new User("Sam", "ilya@mail.com", 31);
        assertThrows(Exception.class, () -> userDao.save(duplicate));
    }
}