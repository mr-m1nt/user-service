package org.example.service;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}