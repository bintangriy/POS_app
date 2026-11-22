package com.posapp.dao;

import com.posapp.model.Category;
import java.util.List;

public interface CategoryDao {
    Category findById(int id);
    Category findByName(String name);
    List<Category> findAll();
    boolean insert(Category category);
    boolean update(Category category);
    boolean delete(int id);
}

