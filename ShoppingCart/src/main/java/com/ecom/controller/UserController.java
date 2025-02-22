package com.ecom.controller;

import com.ecom.model.Cart;
import com.ecom.model.MyUser;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductOrderService;
import com.ecom.service.UserService;
import com.ecom.utils.CommonUtils;
import com.ecom.utils.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        return "redirect:/product/" + productId;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal principal, Model model) {
        MyUser myUser = commonUtils.getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(myUser.getId());
        model.addAttribute("carts", carts);
        if (!carts.isEmpty()) {
            model.addAttribute("netPrice", carts.get(carts.size() - 1).getNetPrice());
        } else {
            model.addAttribute("netPrice", 0);
        }
        return "/user/cart";
    }

    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            MyUser myUser = userService.getUserByEmail(email);
            model.addAttribute("user", myUser);
            int cartCount = cartService.getCount(myUser.getId());
            model.addAttribute("cartCount", cartCount);
        } else {
            model.addAttribute("user", null);
        }
        model.addAttribute("categories", categoryService.getAllActiveCategories());
    }

    @GetMapping("/quantityUpdate")
    public String quantityUpdate(@RequestParam String symbol, @RequestParam int cartId) {
        cartService.updateQuantity(symbol, cartId);
        return "redirect:/user/cart";
    }

    @GetMapping("/order")
    public String orderPage(Model model, Principal principal) {
        MyUser myUser = commonUtils.getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(myUser.getId());
        model.addAttribute("carts", carts);
        if (!carts.isEmpty()) {
            model.addAttribute("netPrice", carts.get(carts.size() - 1).getNetPrice());
            model.addAttribute("subtotal", (carts.get(carts.size() - 1).getNetPrice()) + 250 + 100);
        } else {
            model.addAttribute("netPrice", 0);
            model.addAttribute("subtotal", 0);
        }
        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest, Principal principal) throws Exception {
        MyUser myUser = userService.getUserByEmail(principal.getName());
        productOrderService.saveOrder(myUser.getId(), orderRequest);
        return "redirect:/user/order_success";
    }

    @GetMapping("/order_success")
    public String loadOrderSuccessPage() {
        return "/User/order_success";
    }

    @GetMapping("/orders")
    public String loadMyOrdersPage(Principal principal, Model model) {
        MyUser myUser = userService.getUserByEmail(principal.getName());
        model.addAttribute("orders", productOrderService.getOrdersByUserId(myUser.getId()));
        return "/User/my_orders";
    }

    @GetMapping("/update-status")
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
        return "redirect:/user/orders";
    }

    @GetMapping("/profile")
    public String loadProfilePage() {
        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateUserProfile(@ModelAttribute MyUser myUser, @RequestParam MultipartFile image, HttpSession session) {

        MyUser updateMyUser = userService.updateUserProfile(myUser, image);

        if (ObjectUtils.isEmpty(updateMyUser)) {
            session.setAttribute("errorMsg", "Failed to update user");
        } else {
            session.setAttribute("succMsg", "User has been successfully updated");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String resetPassword(@RequestParam String currentPassword, @RequestParam String password,
                                @RequestParam String confirmPassword, Principal principal, HttpSession session) {

        MyUser myUser = userService.getUserByEmail(principal.getName());

        if (!ObjectUtils.isEmpty(myUser)) {
            if (!passwordEncoder.matches(currentPassword, myUser.getPassword())) {
                session.setAttribute("errorMsg", "That is not your current password");
            } else {
                if (!password.equals(confirmPassword)) {
                    session.setAttribute("errorMsg", "Passwords do not match");
                    System.out.println("Password: " + password);
                    System.out.println("Confirm Password: " + confirmPassword);
                } else {
                    myUser.setPassword(passwordEncoder.encode(password));
                    userService.updateUser(myUser);
                    session.setAttribute("succMsg", "Password has been successfully changed");
                }
                return "redirect:/user/profile";
            }
        } else {
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/user/profile";
    }
}












