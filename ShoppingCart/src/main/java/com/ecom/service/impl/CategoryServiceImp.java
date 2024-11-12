package com.ecom.service.impl;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // tells spring to manage the class as bean
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Boolean categoryExists(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
