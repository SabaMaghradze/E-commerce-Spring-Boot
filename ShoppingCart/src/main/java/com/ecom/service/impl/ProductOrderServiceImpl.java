package com.ecom.service.impl;

import com.ecom.model.*;
import com.ecom.repository.CartRepo;
import com.ecom.repository.ProductOrderRepo;
import com.ecom.repository.UserRepo;
import com.ecom.service.ProductOrderService;
import com.ecom.utils.CommonUtils;
import com.ecom.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderRepo productOrderRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CommonUtils commonUtils;

    @Override
    public void saveOrder(int userId, OrderRequest orderRequest) throws Exception {

        List<Cart> carts = cartRepo.findCartByUserId(userId);

        for (Cart cart : carts) {

            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalTime.now());

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

            ProductOrder saveOrder = productOrderRepo.save(order);

            commonUtils.sendMailForOrder(saveOrder, saveOrder.getStatus());

            cartRepo.delete(cart);
        }
    }

    @Override
    public List<ProductOrder> getOrdersByUserId(int userId) {
        List<ProductOrder> userOrders = productOrderRepo.findByUserId(userId);
        return userOrders;
    }

    @Override
    public ProductOrder updateOrderStatus(int orderId, String status) {

        Optional<ProductOrder> order = productOrderRepo.findById(orderId);

        if (!ObjectUtils.isEmpty(order)) {
            ProductOrder orderItself = order.get();
            orderItself.setStatus(status);
            orderItself = productOrderRepo.save(orderItself);
            return orderItself;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return productOrderRepo.findAll();
    }

    @Override
    public ProductOrder getOrderById(String orderId) {
        return productOrderRepo.findByOrderId(orderId);
    }
}










