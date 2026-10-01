package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.event.UserEvent;
import org.example.kafka.UserKafkaProducer;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImplements implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserKafkaProducer kafkaProducer;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email уже занят: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        kafkaProducer.sendEvent(new UserEvent("CREATE", saved.getEmail()));
        log.info("Событие CREATE отправлено в Kafka для email: {}", saved.getEmail());
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));

        userMapper.updateEntityFromRequest(request, user);
        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // ВАЖНО: сначала находим пользователя, чтобы получить email
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));
        String email = user.getEmail();  // ← Сохраняем email ДО удаления

        userRepository.deleteById(id);

        //ОТПРАВКА СОБЫТИЯ В KAFKA
        kafkaProducer.sendEvent(new UserEvent("DELETE", email));
        log.info("Событие DELETE отправлено в Kafka для email: {}", email);

    }
}