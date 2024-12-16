package com.ecom.service;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

import java.util.List;

public interface ProductOrderService {
    public void saveOrder(int userId, OrderRequest orderRequest) throws Exception;
    public List<ProductOrder> getOrdersByUserId(int userId);
    public ProductOrder updateOrderStatus(int orderId, String status);
    public List<ProductOrder> getAllOrders();
}
