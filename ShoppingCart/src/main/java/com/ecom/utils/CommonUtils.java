package com.ecom.utils;

import com.ecom.model.ProductOrder;
import com.ecom.model.User;
import com.ecom.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Component
public class CommonUtils {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    // for resetting password

    public Boolean sendMail(String url, String recipientEmail) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("test@gmail.com", "Shopping Cart");
        helper.setTo(recipientEmail);

        String content = "<p>Hello!</p>" + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password</p>" + "<p><a href=\"" + url
                + "\">Change my password</a></p>";

        helper.setSubject("Reset your password");
        helper.setText(content, true);
        mailSender.send(message);

        return true;
    }

    public String generateUrl(HttpServletRequest req) {
        String siteUrl = req.getRequestURL().toString();
        return siteUrl.replace(req.getServletPath(), "");
    }

    String msg = null;

    public Boolean sendMailForOrder(ProductOrder order, String status) throws MessagingException, UnsupportedEncodingException {

        msg = "<p>[[name]]</p><br><p>Order has been [[orderStatus]]</br>.</p"
                + "<p>Product Details: </p>"
                + "<p>Name: [[productName]]</p>"
                + "<p>Category: [[category]]</p>"
                + "<p>Quantity: [[quantity]]</p>"
                + "<p>Price: [[price]]</p>"
                + "<p>Payment Method: [[paymentMethod]]</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("test@gmail.com", "Shopping Cart");
        helper.setTo(order.getOrderAddress().getEmail());

        msg = msg.replace("[[name]]", order.getOrderAddress().getFirstName());
        msg = msg.replace("[[orderStatus]]", status);
        msg = msg.replace("[[productName]]", order.getProduct().getTitle());
        msg = msg.replace("[[category]]", order.getProduct().getCategory());
        msg = msg.replace("[[quantity]]", order.getQuantity().toString());
        msg = msg.replace("[[price]]", order.getPrice().toString());
        msg = msg.replace("[[paymentMethod]]", order.getPaymentType());

        helper.setSubject("Product Order Status");
        helper.setText(msg, true);
        mailSender.send(message);

        return true;
    }

    public User getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        return user;
    }
}
