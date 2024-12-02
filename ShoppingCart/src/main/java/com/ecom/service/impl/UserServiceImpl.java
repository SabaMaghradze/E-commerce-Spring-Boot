package com.ecom.service.impl;

import com.ecom.model.User;
import com.ecom.repository.UserRepo;
import com.ecom.service.UserService;
import com.ecom.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        user.setIsEnabled(true);
        user.setAccNonLocked(true);
        user.setNumberOfFailedAttempts(0);
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

    @Override
    public void increaseFailedAttempts(User user) {
        int attempt = user.getNumberOfFailedAttempts() + 1;
        user.setNumberOfFailedAttempts(attempt);
        userRepo.save(user);
    }

    @Override
    public void lockAccount(User user) {
        user.setAccNonLocked(false);
        user.setLockTime(new Date());
        userRepo.save(user);
    }

    public Boolean unlockAcc(User user) {

        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstants.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (unlockTime < currentTime) {
            user.setAccNonLocked(true);
            user.setLockTime(null);
            user.setNumberOfFailedAttempts(0);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    public void resetAttempt(int id) {

    }
}
