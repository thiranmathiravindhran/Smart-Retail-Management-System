
package com.smartretail.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartretail.service.UserAccountService;

@Controller
public class AuthController {

    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    // Open login page
    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    // Open registration page
    @GetMapping("/register")
    public String registerPage() {
        return "redirect:/register.html";
    }

    // Register new user
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Passwords do not match.");
            return "redirect:/register.html";
        }

        try {
            userAccountService.register(
                    fullName,
                    username,
                    email,
                    password);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Registration successful. Please sign in.");

            return "redirect:/login.html";

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());

            return "redirect:/register.html";
        }
    }

    // Logout page
    @GetMapping("/logout")
    public String logoutPage() {
        return "redirect:/login.html?logout";
    }
}
