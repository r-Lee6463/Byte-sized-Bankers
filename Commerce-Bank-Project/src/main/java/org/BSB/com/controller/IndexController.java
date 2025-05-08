package org.BSB.com.controller;

import java.security.Principal;

import org.BSB.com.entity.User;
import org.BSB.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model, Principal principal) {
        if (principal == null) {
            // not logged in
            return "redirect:/login";
        }

        // `principal.getName()` is the email, since you wired formLogin().usernameParameter("email")
        String email = principal.getName();

        // Make sure your UserService exposes this:
        // Optional<User> findByEmail(String email)
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));

        model.addAttribute("currentUser", user);
        return "dashboard";
    }
}
