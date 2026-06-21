package org.example.service;

import org.example.Dao.UserDao;
import org.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class UserServiceImplements implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplements.class);
    private final UserDao userDao;
    public UserServiceImplements(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User createUser(String name, String email, Integer age) {
        validateName(name);
        validateEmail(email);
        validateAge(age);
        User user = new User(name, email, age);
        userDao.save(user);
        logger.info("User saved: {}", email);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive number");
        }
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(Long id, String newName, String newEmail, Integer newAge) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive number");
        }
        User user = userDao.findById(id)
                .orElseThrow(() -> new RuntimeException("User ID  " + id + " not found"));
        if (newName != null && !newName.isBlank()) {
            validateName(newName);
            user.setName(newName);
        }
        if (newEmail != null && !newEmail.isBlank()) {
            validateEmail(newEmail);
            user.setEmail(newEmail);
        }
        if (newAge != null) {
            validateAge(newAge);
            user.setAge(newAge);
        }
        userDao.update(user);
        logger.info("User updated: ID {}", id);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive number");
        }

        Optional<User> user = userDao.findById(id);
        if (user.isPresent()) {
            userDao.delete(id);
            logger.info("User deleted: ID {}", id);
            return true;
        }
        return false;
    }
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("The name is too long (max 100 characters)");
        }
    }
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @");
        }
    }
    private void validateAge(Integer age) {
        if (age == null) {
            throw new IllegalArgumentException("Age cannot be null");
        }
        if (age < 0 || age > 100) {
            throw new IllegalArgumentException("Age must be between 0 and 100");
        }
    }
}