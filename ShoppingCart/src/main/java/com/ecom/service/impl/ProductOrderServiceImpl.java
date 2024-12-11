package com.ecom.service.impl;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepo;
import com.ecom.repository.ProductOrderRepo;
import com.ecom.service.ProductOrderService;
import com.ecom.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderRepo productOrderRepo;

    @Autowired
    private CartRepo cartRepo;

    @Override
    public void saveOrder(int userId, OrderRequest orderRequest) {

        List<Cart> carts = cartRepo.findCartByUserId(userId);

        for (Cart cart : carts) {

            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentMethod());

            OrderAddress orderAddress = new OrderAddress();

            orderAddress.setFirstName(orderRequest.getFirstName());
            orderAddress.setLastName(orderRequest.getLastName());
            orderAddress.setEmail(orderRequest.getEmail());
            orderAddress.setMobileNumber(orderRequest.getMobileNumber());
            orderAddress.setPincode(orderRequest.getPincode());
            orderAddress.setCity(orderRequest.getCity());
            orderAddress.setState(orderRequest.getState());
            orderAddress.setAddress(orderRequest.getAddress());

            order.setOrderAddress(orderAddress);

            productOrderRepo.save(order);
        }
    }
}










