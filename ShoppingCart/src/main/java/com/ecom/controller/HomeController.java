package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.utils.CommonUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Name;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;

    @GetMapping("")
    public String index(Model model) {

        List<Category> allActiveCategories = categoryService.getAllActiveCategories().stream()
                .limit(6)
                .toList();
        List<Product> allActiveProducts = productService.getActiveProducts("").stream()
                .limit(8)
                .toList();

        model.addAttribute("categories", allActiveCategories);
        model.addAttribute("products", allActiveProducts);

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
            User user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            int cartCount = cartService.getCount(user.getId());
            model.addAttribute("cartCount", cartCount);
        } else {
            model.addAttribute("user", null);
        }
        model.addAttribute("categories", categoryService.getAllActiveCategories());
    }

    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "category", defaultValue = "") String category,
                           @RequestParam(name = "pageN", defaultValue = "0") Integer pageN,
                           @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {

        model.addAttribute("paramValue", category);

        List<Category> categories = categoryService.getAllActiveCategories();
        model.addAttribute("activeCategories", categories);

        Page<Product> page = productService.getAllActiveProductsPagination(pageN, pageSize, category);
        List<Product> products = page.getContent();
        model.addAttribute("activeProducts", products);

        model.addAttribute("pageN", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, @RequestParam("profileImage") MultipartFile profileImage, HttpSession session) {

        System.out.println("User Data: " + user);

        if (user == null) {
            session.setAttribute("errorMsg", "Failed to register: User object is null");
            return "redirect:/register";
        }

        String imageName = profileImage.isEmpty() ? "default.jpg" : profileImage.getOriginalFilename();
        user.setProfileImage(imageName);

        User saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            try {
                File saveFolder = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFolder.getAbsolutePath() + File.separator + "profile_img" + File.separator + profileImage.getOriginalFilename());
                Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException exc) {
                System.out.println(exc + ": " + exc.getMessage());
            }
            session.setAttribute("succMsg", "Registration successful");
        } else {
            session.setAttribute("errorMsg", "Failed to register: internal server error");
        }
        return "redirect:/register";
    }

    // forgot password part

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest req) throws MessagingException, UnsupportedEncodingException {
        User user = userService.getUserByEmail(email);

        if (ObjectUtils.isEmpty(user)) {
            session.setAttribute("errorMsg", "Invalid Email");
        } else {
            String resetToken = UUID.randomUUID().toString();
            userService.updateResetToken(email, resetToken);
            String url = commonUtils.generateUrl(req) + "/reset-password?token=" + resetToken;

            Boolean sendMail = commonUtils.sendMail(url, email);
            if (sendMail) {
                session.setAttribute("succMsg", "Please check your email, password reset link has been sent");
            } else {
                session.setAttribute("errorMsg", "Something went wrong, failed to send mail");
            }
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {

        User user = userService.getUserByResetToken(token);

        if (user == null) {
            model.addAttribute("msg", "Your link is invalid or expired.");
            return "message";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String password,
                                       @RequestParam String confirmPassword, HttpSession session,
                                       Model model) {

        if (!password.equalsIgnoreCase(confirmPassword)) {
            session.setAttribute("errorMsg", "Passwords do not match");
            return "redirect:/reset-password?token=" + token;
        }

        User user = userService.getUserByResetToken(token);

        if (user == null) {
            session.setAttribute("errorMsg", "Your link is invalid or expired.");
            model.addAttribute("msg", "Your link is invalid or expired.");
            return "message";
        } else {
            user.setPassword(passwordEncoder.encode(password));
            user.setResetToken(null);
            userService.updateUser(user);
            session.setAttribute("succMsg", "Password has been changed successfully");
            model.addAttribute("msg", "Password has been changed successfully");
            return "message";
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "ch") String ch, Model model) {

        System.out.println("Search keyword: " + ch);

        List<Product> searchProducts = productService.searchProduct(ch);
        System.out.println(searchProducts.get(0).getTitle());
        model.addAttribute("activeProducts", searchProducts);

        List<Category> categories = categoryService.getAllActiveCategories();
        model.addAttribute("activeCategories", categories);

        model.addAttribute("paramValue", "");

        return "product";
    }
}

















