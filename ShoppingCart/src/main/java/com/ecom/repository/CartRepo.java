package com.ecom.repository;

import com.ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart, Integer> {
    public Cart findByProductIdAndUserId(Integer productId, Integer userId);
    public int countByUserId(int userId);
    public List<Cart> getCartsByUserId(int userId);
    public List<Cart> findCartByUserId(int userId);
}
