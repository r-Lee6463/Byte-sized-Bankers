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
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Controller
public class TransactionController {

    Logger logger = Logger.getLogger(TransactionController.class.getName());

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/transaction")
    public String transaction(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.info("No user in session, redirecting to login.");
            return "redirect:/login";
        }

        // Set the start and end dates for October
        int year = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(year, Month.OCTOBER, 1);
        LocalDate endDate = LocalDate.of(year, Month.OCTOBER, 31);

        // Fetch transactions within the October date range
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetween(currentUser, startDate, endDate);

        // Sort transactions by date in ascending order
        transactions.sort(Comparator.comparing(Transaction::getDate));

        model.addAttribute("transactions", transactions);
        model.addAttribute("currentUser", currentUser);

        // Calculate cumulative balance
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        List<String> dates = new ArrayList<>();
        List<BigDecimal> cumulativeBalances = new ArrayList<>();

        BigDecimal cumulativeBalance = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            cumulativeBalance = cumulativeBalance.add(transaction.getAmount());
            dates.add(transaction.getDate().format(formatter));
            cumulativeBalances.add(cumulativeBalance);
        }

        model.addAttribute("dates", dates);
        model.addAttribute("balances", cumulativeBalances);

        return "transaction";
    }
}
