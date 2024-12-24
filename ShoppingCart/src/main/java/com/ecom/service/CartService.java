package com.ecom.service;

import com.ecom.model.Cart;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CartService {
    Cart saveCart(Integer productId, Integer userId);

    List<Cart> getCartsByUser(Integer userId);

    int getCount(int userId);

    void updateQuantity(String symbol, int cartId);
}
