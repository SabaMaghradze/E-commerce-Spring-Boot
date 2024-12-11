package com.ecom.service;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

public interface ProductOrderService {
    public void saveOrder(int userId, OrderRequest orderRequest);
}
