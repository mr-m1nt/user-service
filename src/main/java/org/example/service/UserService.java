package org.example.service;

import org.example.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String email, Integer age);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, String newName, String newEmail, Integer newAge);
    boolean deleteUser(Long id);
}