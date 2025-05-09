package org.BSB.com.controller;

import jakarta.servlet.http.HttpSession;
import org.BSB.com.entity.User;
import org.BSB.com.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found for " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("loginError", "Invalid credentials");
            return "login";
        }

        // success!
        session.setAttribute("loggedInUser", user);
        return "redirect:/dashboard";
    }
}