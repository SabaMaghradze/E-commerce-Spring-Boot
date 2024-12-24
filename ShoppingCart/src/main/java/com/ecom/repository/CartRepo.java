package com.ecom.repository;

import com.ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart, Integer> {
    Cart findByProductIdAndUserId(Integer productId, Integer userId);

    int countByUserId(int userId);

    List<Cart> getCartsByUserId(int userId);

    List<Cart> findCartByUserId(int userId);
}
