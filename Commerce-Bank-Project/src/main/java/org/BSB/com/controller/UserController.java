package org.BSB.com.controller;

import java.security.Principal;

import jakarta.validation.Valid;

import org.BSB.com.dto.AccountUpdateDto;
import org.BSB.com.entity.User;
import org.BSB.com.service.StorageService;
import org.BSB.com.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
public class UserController {

    private final UserService userService;
    private final StorageService storageService;

    public UserController(UserService userService,
            StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping("/edit")
    public String showUpdateForm(Model model, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));

        AccountUpdateDto dto = new AccountUpdateDto();
        dto.setEmail(currentUser.getEmail());
        model.addAttribute("accountUpdateDto", dto);
        return "account-edit";
    }

    @PostMapping("/account/edit")
    public String updateProfile(
            Principal principal,
            @ModelAttribute("accountUpdateDto") @Valid AccountUpdateDto dto,
            BindingResult result,
            @RequestParam(value = "profileImage", required = false) MultipartFile file,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            return "account-edit";
        }

        String filename = null;
        if (file != null && !file.isEmpty()) {
            filename = storageService.store(file);
        }

        userService.updateProfile(
                principal.getName(),
                dto,
                filename);

        ra.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/account";
    }
}
