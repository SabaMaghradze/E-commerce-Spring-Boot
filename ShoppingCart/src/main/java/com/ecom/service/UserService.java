package com.ecom.service;

import com.ecom.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    public User saveUser(User user);
    public User getUserByEmail(String email);
    public List<User> getAllUsers();
    public List<User> getUserByRole(String role);
    public Boolean updateAccountStatus(Boolean status, int id);
    public void increaseFailedAttempts(User user);
    public void lockAccount(User user);
    public Boolean unlockAcc(User user);
    public void resetAttempt(int userId);
    public void updateResetToken(String email, String resetToken);
    public User getUserByResetToken(String token);
    public User updateUser(User user);
    public User updateUserProfile(User user, MultipartFile image);
}
