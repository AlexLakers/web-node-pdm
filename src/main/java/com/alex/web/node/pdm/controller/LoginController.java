package com.alex.web.node.pdm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * This class provides endpoint to handle login-request from user.
 * It represents a login page.
 */

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Model model,
                        @RequestHeader(required = false) String referer){
        model.addAttribute("referer", referer);
        return "user/login";
    }
}
