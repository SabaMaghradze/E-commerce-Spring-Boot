package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
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
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registration() {
        return "registration";
    }

    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            model.addAttribute("user", userService.getUserByEmail(email));
        }
        model.addAttribute("categories", categoryService.getAllActiveCategories());
    }

    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "category", defaultValue = "") String category) {
        model.addAttribute("activeCategories", categoryService.getAllActiveCategories());
        model.addAttribute("activeProducts", productService.getActiveProducts(category));
        model.addAttribute("paramValue", category);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, @RequestParam("profilePic") MultipartFile profilePic, HttpSession session) {

        String imageName = profilePic.isEmpty() ? "default.jpg" : profilePic.getOriginalFilename();
        user.setProfileImage(imageName);

        User saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            try {
                File saveFolder = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFolder.getAbsolutePath() + File.separator + "profile_img" + File.separator + profilePic.getOriginalFilename());
                System.out.println(path);
                Files.copy(profilePic.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException exc) {
                System.out.println(exc);
            }
            session.setAttribute("succMsg", "Registration successful");
        } else {
            session.setAttribute("errorMsg", "Failed to register: internal server error");
        }
        return "redirect:/register";
    }
}
