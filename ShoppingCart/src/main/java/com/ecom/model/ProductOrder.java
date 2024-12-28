package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.format.annotation.NumberFormat;
import java.time.LocalTime;

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
    private LocalTime orderDate;

    @ManyToOne
    private Product product;

    @NumberFormat(pattern = "#,###.##")
    private Double price;

    private Double quantity;

    @ManyToOne
    private User user;

    private String status;
    private String paymentType;

    @OneToOne
    @Cascade(CascadeType.ALL)
    private OrderAddress orderAddress;
}
















