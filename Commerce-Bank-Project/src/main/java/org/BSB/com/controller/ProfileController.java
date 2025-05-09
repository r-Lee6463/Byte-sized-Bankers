package org.BSB.com.controller;

import jakarta.validation.Valid;
import org.BSB.com.dto.AccountUpdateDto;
import org.BSB.com.entity.User;
import org.BSB.com.service.StorageService;
import org.BSB.com.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/account")
public class ProfileController {

    private final UserService userService;
    private final StorageService storageService;

    public ProfileController(UserService userService,
            StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping
    public String showAccountForm(
            @ModelAttribute("accountUpdateDto") AccountUpdateDto dto,
            BindingResult bindingResult,
            Model model,
            Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        dto.setEmail(user.getEmail());
        model.addAttribute("currentUser", user);
        return "account";
    }

    @PostMapping
    public String updateAccount(
            @Valid @ModelAttribute("accountUpdateDto") AccountUpdateDto dto,
            BindingResult result,
            @RequestParam("profileImage") MultipartFile file,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            User user = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            model.addAttribute("currentUser", user);
            return "account";
        }

        try {
            String filename = null;
            if (file != null && !file.isEmpty()) {
                filename = storageService.store(file);
            }
            userService.updateProfile(principal.getName(), dto, filename);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your profile has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Oops—something went wrong. Please try again.");
        }

        return "redirect:/account";
    }
}