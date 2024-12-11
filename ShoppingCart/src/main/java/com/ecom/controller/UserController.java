package com.ecom.controller;

import com.ecom.model.Cart;
import com.ecom.model.OrderRequest;
import com.ecom.model.User;
import com.ecom.repository.CartRepo;
import com.ecom.repository.UserRepo;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductOrderService;
import com.ecom.service.UserService;
import com.ecom.utils.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductOrderService productOrderService;

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

    @GetMapping("/cart")
    public String loadCartPage(Principal principal, Model model) {
        User user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);
        if (!carts.isEmpty()) {
            model.addAttribute("netPrice", carts.get(carts.size() - 1).getNetPrice());
        } else {
            model.addAttribute("netPrice", 0);
        }
        return "/user/cart";
    }

    private User getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        return user;
    }

    @GetMapping("/quantityUpdate")
    public String quantityUpdate(@RequestParam String symbol, @RequestParam int cartId) {
        cartService.updateQuantity(symbol, cartId);
        return "redirect:/user/cart";
    }

    @GetMapping("/orders")
    public String ordersPage() {
        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        productOrderService.saveOrder(user.getId(), orderRequest);
        return "/User/order_success";
    }
}












