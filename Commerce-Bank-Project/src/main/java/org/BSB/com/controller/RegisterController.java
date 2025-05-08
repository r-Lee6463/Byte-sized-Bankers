package org.BSB.com.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.BSB.com.dto.UserRegistrationDto;
import org.BSB.com.service.EmailExistsException;
import org.BSB.com.service.UserService;

@Controller
public class RegisterController {

    private final UserService userService;
    private static final String REGISTER_VIEW = "register";

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showForm(@ModelAttribute("userDto") UserRegistrationDto dto) {
        return REGISTER_VIEW;
    }

    @PostMapping("/register")
    public String process(
        @ModelAttribute("userDto") @Valid UserRegistrationDto dto,
        BindingResult br
    ) {
        // 1) confirm password match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            br.rejectValue("confirmPassword", "password.mismatch", "Passwords must match");
        }

        // 2) check email uniqueness
        if (!br.hasErrors() && userService.emailExists(dto.getEmail())) {
            br.rejectValue("email", "email.exists", "Email already registered");
        }

        // 3) save if all good
        if (!br.hasErrors()) {
            try {
                userService.registerNewUser(dto);
            } catch (EmailExistsException e) {
                // unlikely now, but just in case of race
                br.rejectValue("email", "email.exists", e.getMessage());
            }
        }

        return br.hasErrors() ? REGISTER_VIEW : "redirect:/login?registered";
    }
}
