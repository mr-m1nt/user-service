package org.example.service;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor  // Lombok создаёт конструктор со всеми final полями
@Transactional(readOnly = true)  // По умолчанию все методы read-only
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional  // Переопределяет readOnly для write-операций
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email уже занят");
        }

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));

        userMapper.updateEntityFromRequest(request, user);
        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Пользователь не найден: " + id);
        }
        userRepository.deleteById(id);
    }
}