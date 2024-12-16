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

import java.util.ArrayList;
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
            cart.setQuantity(1.00);
            cart.setTotalPrice((cart.getQuantity()) * (cart.getProduct().getDiscountPrice()));
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice((cart.getQuantity()) * (cart.getProduct().getDiscountPrice()));
        }
        return cartRepo.save(cart);
    }

    @Override
    public List<Cart> getCartsByUser(Integer userId) {

        List<Cart> carts = cartRepo.getCartsByUserId(userId);

        Double netPrice = 0.0;

        List<Cart> updateCarts = new ArrayList<>();

        for (Cart cart : carts) {
            Double totalPrice = cart.getProduct().getDiscountPrice()*cart.getQuantity();
            cart.setTotalPrice(totalPrice);
            netPrice += totalPrice;
            cart.setNetPrice(netPrice);
            updateCarts.add(cart);
        }
        return carts;
    }

    @Override
    public int getCount(int userId) {
        return cartRepo.countByUserId(userId);
    }

    @Override
    public void updateQuantity(String symbol, int cartId) {

        Cart cart = cartRepo.findById(cartId).get();

        if (cart.getQuantity() > 1) {
            if (symbol.equalsIgnoreCase("minus")) {
                cart.setQuantity(cart.getQuantity() - 1);
            } else if (symbol.equalsIgnoreCase("plus")) {
                cart.setQuantity(cart.getQuantity() + 1);
            }
            cartRepo.save(cart);
        } else {
            if (symbol.equalsIgnoreCase("plus")) {
                cart.setQuantity(cart.getQuantity() + 1);
                cartRepo.save(cart);
            } else if (symbol.equalsIgnoreCase("minus")) {
                cartRepo.delete(cart);
            }
        }
    }
}














