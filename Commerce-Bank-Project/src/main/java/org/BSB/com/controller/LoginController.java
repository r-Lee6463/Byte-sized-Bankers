package org.BSB.com.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.BSB.com.dto.LoginDto;
import org.BSB.com.entity.User;
import org.BSB.com.service.UserService;

@Controller
public class LoginController {

    private final UserService userService;
    private static final String LOGIN_VIEW = "login";

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLogin(@ModelAttribute("loginDto") LoginDto dto) {
        return LOGIN_VIEW;
    }

    @PostMapping("/login")
    public String processLogin(
        @ModelAttribute("loginDto") @Valid LoginDto dto,
        BindingResult br,
        Model model,
        HttpSession session
    ) {
        if (br.hasErrors()) {
            return LOGIN_VIEW;
        }

        Optional<User> optUser = userService.findByEmail(dto.getEmail());
        if (optUser.isEmpty()) {
            br.rejectValue("email", "notfound", "No account found for that email");
            return LOGIN_VIEW;
        }

        User user = optUser.get();
        if (!userService.matchesPassword(dto.getPassword(), user.getPassword())) {
            br.rejectValue("password", "invalid", "Invalid credentials");
            return LOGIN_VIEW;
        }

        // on success, stash user in session (or set up Spring Security)
        session.setAttribute("currentUser", user);
        return "redirect:/dashboard";
    }
}
