package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.dto.user.NewUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") NewUserDto user,
                               Model model,
                               @RequestHeader(required = false,defaultValue = "") String referer) {
        model.addAllAttributes(Map.of("user", user, "referer", referer));
        return "user/registration";
    }
}
