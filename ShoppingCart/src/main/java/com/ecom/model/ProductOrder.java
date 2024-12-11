package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String orderId;
    private Date orderDate;

    @ManyToOne
    private Product product;

    private Double price;
    private int quantity;

    @ManyToOne
    private User user;

    private String status;
    private String paymentType;

    @OneToOne
    @Cascade(CascadeType.ALL)
    private OrderAddress orderAddress;
}
















