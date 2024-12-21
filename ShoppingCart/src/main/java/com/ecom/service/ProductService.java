package com.ecom.service;

import com.ecom.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product saveProduct(Product product);

    List<Product> getAllProducts();

    Boolean deleteProduct(int id);

    Product getProductById(int id);

    List<Product> getActiveProducts(String category);

    List<Product> searchProduct(String ch);

    Page<Product> getAllActiveProductsPagination(Integer pageN, Integer pageSize, String category);
}
