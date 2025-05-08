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

@Controller
public class TransactionController {

    private final TransactionService txService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService txService,
                                 UserService userService) {
        this.txService   = txService;
        this.userService = userService;
    }

    @GetMapping("/transactions")
    public String transactions(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        // load the full User object
        User user = userService.findByEmail(principal.getName())
                               .orElseThrow();

        // fetch this user’s transactions
        List<Transaction> txs = txService.findByUser(user);
        model.addAttribute("transactions", txs);
        model.addAttribute("currentUser",     user);
        return "transaction";
    }
}
