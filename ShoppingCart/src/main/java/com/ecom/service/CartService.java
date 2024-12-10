package com.ecom.service;

import com.ecom.model.Cart;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CartService {
    public Cart saveCart(Integer productId, Integer userId);
    public List<Cart> getCartByUser(Integer userId);
}