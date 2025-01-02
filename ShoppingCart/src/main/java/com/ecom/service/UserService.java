package com.ecom.service;

import com.ecom.model.MyUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    MyUser saveUser(MyUser myUser);

    MyUser getUserByEmail(String email);

    Boolean emailExists(String email);

    List<MyUser> getAllUsers();

    List<MyUser> getUserByRole(String role);

    Boolean updateAccountStatus(Boolean status, int id);

    void increaseFailedAttempts(MyUser myUser);

    void lockAccount(MyUser myUser);

    Boolean unlockAcc(MyUser myUser);

    void updateResetToken(String email, String resetToken);

    MyUser getUserByResetToken(String token);

    MyUser updateUser(MyUser myUser);

    MyUser updateUserProfile(MyUser myUser, MultipartFile image);

    MyUser saveAdmin(MyUser myUser);
}
