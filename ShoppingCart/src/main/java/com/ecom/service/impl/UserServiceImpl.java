package com.ecom.service.impl;

import com.ecom.model.User;
import com.ecom.repository.UserRepo;
import com.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public List<User> getUserByRole(String role) {
        return userRepo.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Boolean status, int id) {
        Optional<User> findByUser = userRepo.findById(id);

        if (findByUser.isPresent()) {
            User user = findByUser.get();
            user.setIsEnabled(status);
            userRepo.save(user);
            return true;
        }
        return false;
    }
}
