package com.autocarepro.controller;

import com.autocarepro.dto.RegisterRequest;
import com.autocarepro.exception.BusinessException;
import com.autocarepro.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(request);
            model.addAttribute("success", "Account created successfully! Please sign in.");
            return "auth/login";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        userService.initiatePasswordReset(email);
        model.addAttribute("success", "If an account exists for that email, a reset link has been sent.");
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, Model model) {
        boolean success = userService.resetPassword(token, password);
        if (success) {
            model.addAttribute("success", "Password reset successful. Please sign in.");
            return "auth/login";
        }
        model.addAttribute("error", "This reset link is invalid or has expired.");
        model.addAttribute("token", token);
        return "auth/reset-password";
    }
}
