package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/category")
    public String category(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct() {
        return "admin/add_product";
    }

    @GetMapping("")
    public String index() {
        return "admin/index";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) {

        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean categoryExists = categoryService.categoryExists(category.getName());

        if (categoryExists) {
            session.setAttribute("errorMsg", "Category name already exists");
        } else {

            Category saveCategory = categoryService.saveCategory(category);

            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("errorMsg", "Failed to save category: internal server error");
            } else {
                try {
                    File saveFolder = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFolder.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                    System.out.println(path);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exc) {
                    System.out.println(exc);
                }
                session.setAttribute("succMsg:", "Category has been saved successfully");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {

        Boolean deleteCategory = categoryService.deleteCategory(id);

        if (deleteCategory) {
            session.setAttribute("succMsg", "Category has been successfully deleted");
        } else {
            session.setAttribute("errorMsg", "Failed to delete category");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));

        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam MultipartFile file, HttpSession session) {

        Category existingCategory = categoryService.getCategoryById(category.getId());
        String imageName = !file.isEmpty() ? file.getOriginalFilename() : existingCategory.getImageName();

        if (!ObjectUtils.isEmpty(existingCategory)) {
            existingCategory.setName(category.getName());
            existingCategory.setIsActive(category.getIsActive());
            existingCategory.setImageName(imageName);
        }

        Category updateCategory = categoryService.saveCategory(existingCategory);

        if (!ObjectUtils.isEmpty(updateCategory)) {
            if (!file.isEmpty()) {
                try {
                    File saveFolder = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFolder.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                    System.out.println(path);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exc) {
                    System.out.println(exc);
                }
            }
            session.setAttribute("succMsg", "Category has been successfully updated");
        } else {
            session.setAttribute("errorMsg", "Something went wrong, internal server error");
        }

        return "redirect:/admin/category";
    }

}


















