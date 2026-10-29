
package com.smartretail.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartretail.entity.UserAccount;
import com.smartretail.service.UserAccountService;

@Controller
public class SettingsController {

    private final UserAccountService userAccountService;

    public SettingsController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/settings")
    public String settings(Authentication authentication, Model model) {
        UserAccount account = userAccountService.getAccount(authentication.getName());

        model.addAttribute("account", account);
        return "settings";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(
            Authentication authentication,
            @RequestParam String fullName,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        try {
            userAccountService.updateProfile(
                    authentication.getName(), fullName, email);

            redirectAttributes.addFlashAttribute(
                    "success", "Profile updated successfully.");

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/settings";
    }

    @PostMapping("/settings/password")
    public String changePassword(
            Authentication authentication,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                    "error", "New passwords do not match.");
            return "redirect:/settings";
        }

        try {
            userAccountService.changePassword(
                    authentication.getName(),
                    currentPassword,
                    newPassword);

            redirectAttributes.addFlashAttribute(
                    "success", "Password changed successfully.");

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/settings";
    }
}
