package com.ecom.config;

import com.ecom.model.User;
import com.ecom.repository.UserRepo;
import com.ecom.service.UserService;
import com.ecom.utils.AppConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException exception) throws IOException, ServletException {

        String email = req.getParameter("username");
        User user = userRepo.findByEmail(email);

        if (user.getIsEnabled()) {
            if (user.getAccNonLocked()) {
                if (user.getNumberOfFailedAttempts() < AppConstants.ATTEMPT_COUNT) {
                    userService.increaseFailedAttempts(user);
                } else {
                    userService.lockAccount(user);
                    exception = new LockedException("Your account has been locked, failed attempt N.3");
                }
            } else {
                if (userService.unlockAcc(user)) {
                    exception = new LockedException("Your account is unlocked, please try again.");
                } else {
                    exception = new LockedException("Your account is locked, please try again later");
                }
            }
        } else {
            exception = new LockedException("Your account is inactive");
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(req, res, exception);
    }
}
