package com.ecom.repository;

import com.ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategory(String category);

    Page<Product> findByCategory(Pageable pageable, String category);

    List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch1, String ch2);

    Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch1, String ch2, Pageable page);

    List<Product> findByIsActiveTrue();

    Page<Product> findByIsActiveTrue(Pageable pageable);
}
