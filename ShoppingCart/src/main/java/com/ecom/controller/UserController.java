package com.ecom.controller;

import com.ecom.model.Cart;
import com.ecom.repository.CartRepo;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            model.addAttribute("user", userService.getUserByEmail(email));
        }
        model.addAttribute("categories", categoryService.getAllActiveCategories());
    }

    public String home() {
        return "User/home";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer productId, @RequestParam Integer userId, HttpSession session) {
        Cart cart = cartService.saveCart(productId, userId);

        if (ObjectUtils.isEmpty(cart)) {
            session.setAttribute("errorMsg", "Failed to save cart");
        } else {
            session.setAttribute("succMsg", "Cart has been successfully saved");
        }

        return "redirect:/product/"+productId;
    }

}
