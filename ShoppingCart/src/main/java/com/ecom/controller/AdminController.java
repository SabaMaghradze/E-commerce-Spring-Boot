package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/category")
    public String category() {
        return "admin/category";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct() {
        return "admin/add_product";
    }

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }
}
