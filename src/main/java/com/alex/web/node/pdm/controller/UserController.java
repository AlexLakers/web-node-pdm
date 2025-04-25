package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.config.security.CustomUserDetails;
import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
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
        log.info("--start 'create user' endpoint--");
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
                           @RequestHeader(required = false) String referer,
                           @AuthenticationPrincipal CustomUserDetails authUser) {
        log.info("--start 'find user by id' endpoint--");
        model.addAllAttributes(Map.of("user", userService.findById(id), "referer", referer));
        return "user/user";
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String findAll(Model model,
                          @AuthenticationPrincipal CustomUserDetails authUser,
                          @ModelAttribute  @RequestHeader(required = false) String referer
    ) {
        log.info("--start 'find users' endpoint--");
        model.addAllAttributes(Map.of("users", userService.findAll(), "referer", referer));
        return "user/users";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')")
    public String update(@PathVariable Long id,
                         @Validated UpdateUserDto user,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal CustomUserDetails authUser) {
        log.info("--start 'update user' endpoint--");
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
                         @AuthenticationPrincipal CustomUserDetails authUser
    ) {
        log.info("--start 'delete user' endpoint--");
        userService.delete(id);
        return "redirect:/users";
    }
}
