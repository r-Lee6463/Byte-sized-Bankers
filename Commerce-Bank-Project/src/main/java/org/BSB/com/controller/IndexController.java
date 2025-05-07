package org.BSB.com.controller;


import jakarta.servlet.http.HttpSession;

import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;
import org.BSB.com.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class IndexController {

    Logger logger = Logger.getLogger(IndexController.class.getName());

    private final TransactionRepository transactionRepository;

    @Autowired
    public IndexController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        logger.info("showDashboard");
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.info("No user in session, redirecting to login.");
            return "redirect:/login";
        }

        BigDecimal accountBalance = transactionRepository.getAccountBalanceByUser(currentUser);
        if (accountBalance == null) {
            accountBalance = BigDecimal.ZERO;
        }

        // Fetch transactions for the current user
        List<Transaction> transactions = transactionRepository.findByUser(currentUser);
        model.addAttribute("transactions", transactions);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("accountBalance", accountBalance);
        return "dashboard";


    }
}
