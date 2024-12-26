package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String mobileNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String password;
    private String profileImage;
    private String role;
    private Boolean isEnabled;
    private Boolean accNonLocked;
    private Integer numberOfFailedAttempts;
    private Date lockTime;
    private String resetToken;
}
