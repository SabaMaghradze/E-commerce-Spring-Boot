package com.ecom.service;

import com.ecom.model.Category;

import java.util.List;

public interface CategoryService {

    Category saveCategory(Category category);

    Boolean categoryExists(String name);

    List<Category> getAllCategories();

    Boolean deleteCategory(int id);

    Category getCategoryById(int id);

    List<Category> getAllActiveCategories();
}
