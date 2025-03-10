package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.config.CustomUserDetails;
import com.alex.web.node.pdm.dto.NewUserDto;
import com.alex.web.node.pdm.dto.UpdateUserDto;
import com.alex.web.node.pdm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


    @PostMapping
    /* @ResponseStatus(HttpStatus.CREATED)*/
    public String create(@Validated NewUserDto user,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/registration";
        }
        userService.create(user);
        return "redirect:/login";
    }

    @GetMapping("/{id}")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')") //move to rest controller
    public String findById(@PathVariable("id") Long id, Model model,
                           @RequestHeader(defaultValue = "/admin/users") String referrer,
                           @AuthenticationPrincipal CustomUserDetails authUser) {
        model.addAllAttributes(Map.of("user", userService.findById(id), "referrer", referrer));
        return "user/user";
    }

    @GetMapping
    /*    @PreAuthorize("hasAuthority('ADMIN')")*/
    public String findAll(Model model, @AuthenticationPrincipal CustomUserDetails authUser) {
        System.out.println(authUser);
        model.addAttribute("users",userService.findAll());
        return "user/users";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')")
    public String update(@PathVariable Long id,
                         @Validated UpdateUserDto user,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal CustomUserDetails authUser) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/users/{id}";
        }
        return "redirect:/users/" + userService.update(id, user).id();

    }

    /* @ResponseStatus(HttpStatus.NO_CONTENT)*/
    @PostMapping("/delete")

    public String delete(@RequestParam("userId") Long id,
                         @AuthenticationPrincipal CustomUserDetails authUser) {
        userService.delete(id);
        return "redirect:/users";
    }
}
