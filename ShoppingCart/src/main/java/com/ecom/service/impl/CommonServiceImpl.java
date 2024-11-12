package com.ecom.service.impl;

import com.ecom.service.CommonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public void removeSessionMessage() {
        HttpServletRequest req = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes()))
                .getRequest();
        HttpSession session = req.getSession();
        session.removeAttribute("succMsg");
        session.removeAttribute("errorMsg");
//        System.out.println("request url: " + req.getRequestURL());
//        System.out.println("port: " + req.getServerPort());
//        System.out.println("servlet path: " + req.getServletPath());
    }
}
