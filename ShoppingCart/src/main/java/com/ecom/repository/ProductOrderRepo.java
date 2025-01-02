package com.ecom.repository;

import com.ecom.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepo extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByMyUserId(int userId);

    ProductOrder findByOrderId(String orderId);
}
