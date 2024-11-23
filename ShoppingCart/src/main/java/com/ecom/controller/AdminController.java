package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import jakarta.servlet.http.HttpSession;
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
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/category")
    public String category(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
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

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Product saveProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(saveProduct)) {
            try {
                File saveFolder = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFolder.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
                System.out.println(path);
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException exc) {
                System.out.println(exc);
            }
            session.setAttribute("succMsg", "Product has been succesfully saved");
        } else {
            session.setAttribute("errorMsg", "Failed to save product, internal server error");
        }
        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {

        Boolean deleteProduct = productService.deleteProduct(id);

        if (deleteProduct) {
            session.setAttribute("succMsg", "Product has been successfully deleted");
        } else {
            session.setAttribute("errorMsg", "Failed to delete product");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/loadEditProduct/{id}")
    public String loadEditProduct(@PathVariable int id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) {

        Product existingProduct = productService.getProductById(product.getId());
        String imageName = !image.isEmpty() ? image.getOriginalFilename() : existingProduct.getImage();

        if (!ObjectUtils.isEmpty(existingProduct)) {

            existingProduct.setImage(imageName);
            existingProduct.setTitle(product.getTitle());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setStock(product.getStock());
            existingProduct.setIsActive(product.getIsActive());

            existingProduct.setDiscount(product.getDiscount());

            Double discount = product.getPrice() * (product.getDiscount() / 100.00);
            Double discountPrice = product.getPrice() - discount;

            existingProduct.setDiscountPrice(discountPrice);
        }

        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("errorMsg", "Invalid discount");
        }

        Product updateProduct = productService.saveProduct(existingProduct);

        if (!ObjectUtils.isEmpty(updateProduct)) {
            if (!image.isEmpty()) {
                try {
                    File saveFolder = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFolder.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
                    System.out.println(path);
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exc) {
                    System.out.println(exc);
                }
            }
            session.setAttribute("succMsg", "Product has been successfully updated");
        } else {
            session.setAttribute("errorMsg", "Something went wrong, internal server error");
        }
        return "redirect:/admin/products";
    }
}

























