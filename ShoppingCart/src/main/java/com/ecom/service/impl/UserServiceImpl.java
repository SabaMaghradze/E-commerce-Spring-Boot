package com.ecom.service.impl;

import com.ecom.model.User;
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
        // to do
    }

    @Override
    public void updateResetToken(String email, String resetToken) {
        User user = userRepo.findByEmail(email);
        user.setResetToken(resetToken);
        userRepo.save(user);
    }

    @Override
    public User getUserByResetToken(String token) {
        return userRepo.findByResetToken(token);
    }

    @Override
    public User updateUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public User updateUserProfile(User user, MultipartFile image) {

        User userExists = userRepo.findById(user.getId()).get();

        if (!image.isEmpty()) {
            userExists.setProfileImage(user.getProfileImage());
        }

        if (!ObjectUtils.isEmpty(userExists)) {
            userExists.setName(user.getName());
            userExists.setEmail(user.getEmail());
            userExists.setMobileNumber(user.getMobileNumber());
            userExists.setState(user.getState());
            userExists.setCity(user.getCity());
            userExists.setAddress(user.getAddress());
            userExists.setPincode(user.getPincode());
            userExists = userRepo.save(userExists);
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
        return userExists;
    }

    @Override
    public User saveAdmin(User user) {
        user.setRole("ROLE_ADMIN");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsEnabled(true);
        user.setAccNonLocked(true);
        user.setNumberOfFailedAttempts(0);
        return userRepo.save(user);
    }
}





