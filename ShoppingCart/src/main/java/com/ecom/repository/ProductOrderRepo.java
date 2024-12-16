package com.ecom.repository;

import com.ecom.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepo extends JpaRepository<ProductOrder, Integer> {
    public List<ProductOrder> findByUserId(int userId);

}
