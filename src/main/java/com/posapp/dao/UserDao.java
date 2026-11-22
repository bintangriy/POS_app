package com.posapp.dao;

import com.posapp.model.User;
import java.util.List;

public interface UserDao {
    User findById(int id);
    User findByUsername(String username);
    List<User> findAll();
    boolean insert(User user);
    boolean update(User user);
    boolean delete(int id);
}

