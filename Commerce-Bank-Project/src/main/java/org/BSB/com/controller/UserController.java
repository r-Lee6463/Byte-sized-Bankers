package org.BSB.com.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.BSB.com.dto.AccountUpdateDto;
import org.BSB.com.entity.User;
import org.BSB.com.service.EmailExistsException;
import org.BSB.com.service.UserService;

@Controller
public class UserController {

    private final UserService userService;
    private static final String ACCOUNT_VIEW  = "account";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // —— Account Update ——
    @GetMapping("/account")
    public String showUpdateForm(Model model, Principal principal) {
        User current = userService.findByUsername(principal.getName());
        AccountUpdateDto dto = new AccountUpdateDto();
        dto.setEmail(current.getEmail());
        model.addAttribute("updateDto", dto);
        return ACCOUNT_VIEW;
    }

    @PostMapping("/account")
    public String processUpdate(
        @ModelAttribute("updateDto") @Valid AccountUpdateDto dto,
        BindingResult br,
        Principal principal
    ) {
        // passwords match?
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            br.rejectValue("confirmPassword", "password.mismatch", "Passwords must match");
        }

        // email uniqueness & save
        if (!br.hasErrors()) {
            try {
                userService.updateUserAccount(principal.getName(), dto);
            } catch (EmailExistsException e) {
                br.rejectValue("email", "email.exists", e.getMessage());
            }
        }

        return br.hasErrors() ? ACCOUNT_VIEW : "redirect:/account?updated";
    }
}