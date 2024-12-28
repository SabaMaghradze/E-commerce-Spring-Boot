package com.ecom.service;

import com.ecom.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    User getUserByEmail(String email);

    Boolean emailExists(String email);

    List<User> getAllUsers();

    List<User> getUserByRole(String role);

    Boolean updateAccountStatus(Boolean status, int id);

    void increaseFailedAttempts(User user);

    void lockAccount(User user);

    Boolean unlockAcc(User user);

    void updateResetToken(String email, String resetToken);

    User getUserByResetToken(String token);

    User updateUser(User user);

    User updateUserProfile(User user, MultipartFile image);

    User saveAdmin(User user);
}
