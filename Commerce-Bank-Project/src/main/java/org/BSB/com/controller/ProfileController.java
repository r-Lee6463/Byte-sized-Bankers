package org.BSB.com.controller;


import jakarta.servlet.http.HttpSession;

import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;
import org.BSB.com.repository.TransactionRepository;
import org.BSB.com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.logging.Logger;

@Controller
public class ProfileController {

    Logger logger = Logger.getLogger(ProfileController.class.getName());

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    @Autowired
    public ProfileController(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.info("No user in session, redirecting to login.");
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);

        return "profile";
    }
}
