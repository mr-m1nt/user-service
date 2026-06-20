package org.example.Dao;

import org.example.entity.User;
import java.util.List;

public interface UserDao {
    void save(User user);
    java.util.Optional<User> findById(Long id);
    List<User> findAll();
    void update(User user);
    void delete(Long id);
}