package com.ecom.service;

import com.ecom.model.User;

import java.util.List;

public interface UserService {
    public User saveUser(User user);
    public User getUserByEmail(String email);
    public List<User> getAllUsers();
    public List<User> getUserByRole(String role);
    public Boolean updateAccountStatus(Boolean status, int id);
}
