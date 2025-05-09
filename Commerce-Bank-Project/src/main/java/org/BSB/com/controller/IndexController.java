package org.BSB.com.controller;

import java.security.Principal;
import java.util.List;

import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;
import org.BSB.com.service.TransactionService;
import org.BSB.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    private final UserService userService;
    private final TransactionService txService;

    @Autowired
    public IndexController(UserService userService,
            TransactionService txService) {
        this.userService = userService;
        this.txService = txService;
    }

    @GetMapping({ "/", "/dashboard" })
    public String showDashboard(
            Model model,
            Principal principal,
            @RequestParam(value = "added", required = false) String added) {
        if (principal == null) {
            return "redirect:/login";
        }
        if (added != null) {
            model.addAttribute("addedMsg", "Transaction added successfully.");
        }

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> recent = txService.findByUser(user);
        model.addAttribute("recentTxs", recent);
        model.addAttribute("currentUser", user);
        return "dashboard";
    }
}
