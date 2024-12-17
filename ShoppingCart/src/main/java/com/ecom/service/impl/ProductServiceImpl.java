package com.ecom.service.impl;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Override
    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(int id) {
        Product product = productRepo.findById(id).orElse(null);
        return product;
    }

    @Override
    public Boolean deleteProduct(int id) {

        Product product = productRepo.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(product)) {
            productRepo.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public List<Product> getActiveProducts(String category) {

        List<Product> main = new ArrayList<>();

        List<Product> products = null;

        if (ObjectUtils.isEmpty(category)) {
            products = productRepo.findAll();
        } else {
            products = productRepo.findByCategory(category);
        }

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getIsActive()) {
                main.add(products.get(i));
            }
        }
        return main;
    }

    @Override
    public List<Product> searchProduct(String ch) {
        return productRepo.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
    }
}











