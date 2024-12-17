package com.ecom.service;

import com.ecom.model.OrderRequest;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;

import java.util.List;

public interface ProductOrderService {

    void saveOrder(int userId, OrderRequest orderRequest) throws Exception;

    List<ProductOrder> getOrdersByUserId(int userId);

    ProductOrder updateOrderStatus(int orderId, String status);

    List<ProductOrder> getAllOrders();

    ProductOrder getOrderById(String orderId);
}
