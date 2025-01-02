package com.ecom.repository;

import com.ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart, Integer> {
    Cart findByProductIdAndMyUserId(Integer productId, Integer userId);

    int countByMyUserId(int userId);

    List<Cart> getCartsByMyUserId(int userId);

    List<Cart> findCartByMyUserId(int userId);
}
