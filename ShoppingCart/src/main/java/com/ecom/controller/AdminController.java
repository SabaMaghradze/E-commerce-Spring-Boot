package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.MyUser;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductOrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.utils.CommonUtils;
import com.ecom.utils.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@Controller
@RequestMapping("/admin/")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    private void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            model.addAttribute("user", userService.getUserByEmail(email));
        }
        model.addAttribute("categories", categoryService.getAllActiveCategories());
    }

    @GetMapping("/profile")
    public String loadProfilePage() {
        return "/admin/profile";
    }

    @GetMapping("/categories")
    public String category(Model model) {
        model.addAttribute("categories", categoryService.getAllActiveCategories());
        return "Admin/category";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "Admin/add_product";
    }

    @GetMapping("")
    public String index() {
        return "Admin/index";
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
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exc) {
                    System.out.println(exc);
                }
                session.setAttribute("succMsg:", "Category has been saved successfully");
            }
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {

        Boolean deleteCategory = categoryService.deleteCategory(id);

        if (deleteCategory) {
            session.setAttribute("succMsg", "Category has been successfully deleted");
        } else {
            session.setAttribute("errorMsg", "Failed to delete category");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "Admin/edit_category";
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
        return "redirect:/admin/categories";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("searchMode", false);
        return "Admin/products";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("file") MultipartFile image, HttpSession session) {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

        product.setImage(imageName);

        if (product.getDiscount() > 0) {
            Double discount = product.getPrice() * (product.getDiscount() / 100.00);
            Double discountPrice = product.getPrice() - discount;
            product.setDiscountPrice(discountPrice);
        } else {
            product.setDiscountPrice(product.getPrice());
        }

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
        return "Admin/edit_product";
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

    @GetMapping("/users")
    public String loadUsers(Model model) {
        model.addAttribute("users", userService.getUserByRole("ROLE_USER"));
        return "Admin/users";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam int id, HttpSession session) {
        Boolean bool = userService.updateAccountStatus(status, id);
        if (bool) {
            session.setAttribute("succMsg", "Account status updated");
        } else {
            session.setAttribute("errorMsg", "Failed to update account status");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/orders")
    public String loadOrdersPage(Model model) {
        model.addAttribute("orders", productOrderService.getAllOrders());
        model.addAttribute("searchMode", false);
        return "Admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam int id, @RequestParam int status, HttpSession session) {

        OrderStatus[] values = OrderStatus.values();
        String statuss = null;

        for (OrderStatus value : values) {
            if (value.getId() == status) {
                statuss = value.getName();
            }
        }

        ProductOrder updateOrder = productOrderService.updateOrderStatus(id, statuss);

        try {
            commonUtils.sendMailForOrder(updateOrder, statuss);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "Failed to update status");
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/search-order")
    public String search(@RequestParam String orderId, Model model, HttpSession session) {

        ProductOrder order = productOrderService.getOrderById(orderId.trim());

        if (ObjectUtils.isEmpty(order)) {
            session.setAttribute("errorMsg", "Wrong ID");
            model.addAttribute("order", null);
        } else {
            model.addAttribute("order", order);
        }

        model.addAttribute("searchMode", true);

        return "/admin/orders";
    }

    @GetMapping("/search-product")
    public String searchProduct(@RequestParam int productId, Model model, HttpSession session) {

        Product product = productService.getProductById(productId);

        if (ObjectUtils.isEmpty(product)) {
            session.setAttribute("errorMsg", "Wrong ID");
            model.addAttribute("product", null);
        } else {
            model.addAttribute("product", product);
        }

        model.addAttribute("searchMode", true);

        return "/admin/products";
    }

    @GetMapping("/add-admin")
    public String loadAddAdminPage() {
        return "/admin/add_admin";
    }

    @PostMapping("/saveAdmin")
    public String saveAdmin(@ModelAttribute MyUser myUser, @RequestParam("profileImage") MultipartFile profileImage, HttpSession session) {

        System.out.println("User Data: " + myUser);

        if (myUser == null) {
            session.setAttribute("errorMsg", "Failed to register: User object is null");
            return "redirect:/register";
        }

        String imageName = profileImage.isEmpty() ? "default.jpg" : profileImage.getOriginalFilename();
        myUser.setProfileImage(imageName);

        MyUser saveMyUser = userService.saveAdmin(myUser);

        if (!ObjectUtils.isEmpty(saveMyUser)) {
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
        return "redirect:/admin/add-admin";
    }

    @PostMapping("/update-profile")
    public String updateUserProfile(@ModelAttribute MyUser myUser, @RequestParam MultipartFile image, HttpSession session) {

        MyUser updateMyUser = userService.updateUserProfile(myUser, image);

        if (ObjectUtils.isEmpty(updateMyUser)) {
            session.setAttribute("errorMsg", "Failed to update user");
        } else {
            session.setAttribute("succMsg", "User has been successfully updated");
        }
        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String resetPassword(@RequestParam String currentPassword, @RequestParam String newPassword,
                                @RequestParam String confirmPassword, Principal principal, HttpSession session) {

        MyUser myUser = commonUtils.getLoggedInUserDetails(principal);

        if (!ObjectUtils.isEmpty(myUser)) {
            if (!passwordEncoder.matches(currentPassword, myUser.getPassword())) {
                session.setAttribute("errorMsg", "That is not your current password");
                return "redirect:/admin/profile";
            } else {
                if (newPassword.equals(confirmPassword)) {
                    myUser.setPassword(passwordEncoder.encode(newPassword));
                    MyUser updateMyUser = userService.updateUser(myUser);
                    if (ObjectUtils.isEmpty(updateMyUser)) {
                        session.setAttribute("errorMsg", "Something went wrong, internal server error");
                    } else {
                        session.setAttribute("succMsg", "Password has been successfully updated");
                    }
                } else {
                    session.setAttribute("errorMsg", "Passwords do not match");
                }
            }
        } else {
            session.setAttribute("erroMsg", "You are not authenticated, please log in");
        }
        return "redirect:/admin/profile";
    }
}

























