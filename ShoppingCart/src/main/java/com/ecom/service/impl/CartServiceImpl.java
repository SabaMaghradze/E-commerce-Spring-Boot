package com.ecom.service.impl;


import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.repository.CartRepo;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepo;
import com.ecom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        User user = userRepo.findById(userId).get();
        Product product = productRepo.findById(productId).get();

        Cart cartStatus = cartRepo.findByProductIdAndUserId(productId, userId);

        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice((cart.getQuantity()) * (cart.getProduct().getDiscountPrice()));
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice((cart.getQuantity()) * (cart.getProduct().getDiscountPrice()));
        }
        return cartRepo.save(cart);
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {
        return List.of();
    }
}














