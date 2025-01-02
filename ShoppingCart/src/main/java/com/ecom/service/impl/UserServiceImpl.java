package com.ecom.service.impl;

import com.ecom.model.MyUser;
import com.ecom.repository.UserRepo;
import com.ecom.service.UserService;
import com.ecom.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public MyUser saveUser(MyUser myUser) {
        myUser.setRole("ROLE_USER");
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
        myUser.setIsEnabled(true);
        myUser.setAccNonLocked(true);
        myUser.setNumberOfFailedAttempts(0);
        return userRepo.save(myUser);
    }

    @Override
    public MyUser getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    public List<MyUser> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public List<MyUser> getUserByRole(String role) {
        return userRepo.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Boolean status, int id) {

        Optional<MyUser> findByUser = userRepo.findById(id);

        if (findByUser.isPresent()) {
            MyUser myUser = findByUser.get();
            myUser.setIsEnabled(status);
            userRepo.save(myUser);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempts(MyUser myUser) {
        int attempt = myUser.getNumberOfFailedAttempts() + 1;
        myUser.setNumberOfFailedAttempts(attempt);
        userRepo.save(myUser);
    }

    @Override
    public void lockAccount(MyUser myUser) {
        myUser.setAccNonLocked(false);
        myUser.setLockTime(new Date());
        userRepo.save(myUser);
    }

    public Boolean unlockAcc(MyUser myUser) {

        long lockTime = myUser.getLockTime().getTime();
        long unlockTime = lockTime + AppConstants.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (unlockTime < currentTime) {
            myUser.setAccNonLocked(true);
            myUser.setLockTime(null);
            myUser.setNumberOfFailedAttempts(0);
            userRepo.save(myUser);
            return true;
        }
        return false;
    }

    @Override
    public void updateResetToken(String email, String resetToken) {
        MyUser myUser = userRepo.findByEmail(email);
        myUser.setResetToken(resetToken);
        userRepo.save(myUser);
    }

    @Override
    public MyUser getUserByResetToken(String token) {
        return userRepo.findByResetToken(token);
    }

    @Override
    public MyUser updateUser(MyUser myUser) {
        return userRepo.save(myUser);
    }

    @Override
    public MyUser updateUserProfile(MyUser myUser, MultipartFile image) {

        MyUser myUserExists = userRepo.findById(myUser.getId()).get();

        if (!image.isEmpty()) {
            myUserExists.setProfileImage(myUser.getProfileImage());
        }

        if (!ObjectUtils.isEmpty(myUserExists)) {
            myUserExists.setName(myUser.getName());
            myUserExists.setEmail(myUser.getEmail());
            myUserExists.setMobileNumber(myUser.getMobileNumber());
            myUserExists.setState(myUser.getState());
            myUserExists.setCity(myUser.getCity());
            myUserExists.setAddress(myUser.getAddress());
            myUserExists.setPincode(myUser.getPincode());
            myUserExists = userRepo.save(myUserExists);
        }

        if (!image.isEmpty()) {
            try {
                File saveFolder = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFolder.getAbsolutePath() + File.separator + "profile_img" + File.separator + image.getOriginalFilename());
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return myUserExists;
    }

    @Override
    public MyUser saveAdmin(MyUser myUser) {
        myUser.setRole("ROLE_ADMIN");
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
        myUser.setIsEnabled(true);
        myUser.setAccNonLocked(true);
        myUser.setNumberOfFailedAttempts(0);
        return userRepo.save(myUser);
    }
}





