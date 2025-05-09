package org.BSB.com.controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.BSB.com.dto.TransactionDto;
import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;
import org.BSB.com.service.TransactionService;
import org.BSB.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TransactionController {

    private final TransactionService txService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService txService,
            UserService userService) {
        this.txService = txService;
        this.userService = userService;
    }

    /**
     * List transactions, optionally filtering by category.
     */
    @GetMapping("/transactions")
    public String listTransactions(
            Model model,
            Principal principal,
            @RequestParam(value = "category", required = false) String category) {
        // load user
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // fetch txs
        List<Transaction> txs = (category == null || category.isBlank())
                ? txService.findByUser(user)
                : txService.findByUserAndCategory(user, category);

        // fetch distinct categories for the filter dropdown
        List<String> cats = txService.getCategoriesForUser(user);

        model.addAttribute("transactions", txs);
        model.addAttribute("categories", cats);
        model.addAttribute("currentUser", user);
        model.addAttribute("selectedCat", category);
        return "transaction";
    }

    /**
     * Show the “Add Transaction” form.
     */
    @GetMapping("/transactions/add")
    public String showAddForm(Model model) {
        model.addAttribute("transactionDto", new org.BSB.com.dto.TransactionDto());
        return "add-transaction";
    }

    /**
     * Handle new-transaction submissions.
     */
    @PostMapping("/transactions/add")
    public String addTransaction(
            @ModelAttribute("transactionDto") @Valid TransactionDto dto,
            BindingResult br,
            Model model,
            Principal principal) {
        // 1) manual checks for missing values
        if (dto.getAmount() == null) {
            br.rejectValue("amount", "NotNull", "Amount is required");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            br.rejectValue("description", "NotBlank", "Description is required");
        }
        if (dto.getCategory() == null || dto.getCategory().isBlank()) {
            br.rejectValue("category", "NotBlank", "Category is required");
        }
        if ("Other".equals(dto.getCategory())
                && (dto.getCustomCategory() == null || dto.getCustomCategory().isBlank())) {
            br.rejectValue("customCategory", "NotBlank", "Please enter a custom category");
        }
        if (dto.getDate() == null) {
            br.rejectValue("date", "NotNull", "Date is required");
        }

        // If any binding or manual errors, re‐show the form
        if (br.hasErrors()) {
            // ensure categories list is populated again for the dropdown
            model.addAttribute("categories", txService.getCategoriesForUser(
                    userService.findByEmail(principal.getName()).orElseThrow()));
            return "add-transaction";
        }

        // 2) build the Transaction
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String cat = "Other".equals(dto.getCategory())
                ? dto.getCustomCategory().trim()
                : dto.getCategory();

        Transaction tx = new Transaction();
        tx.setAmount(dto.getAmount());
        tx.setDescription(dto.getDescription());
        tx.setCategory(cat);
        tx.setDate(dto.getDate());
        tx.setUser(user);

        // 3) catch any DB integrity errors
        try {
            txService.save(tx);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            br.reject("saveError", "Failed to save transaction. Please check your inputs.");
            model.addAttribute("categories", txService.getCategoriesForUser(user));
            return "add-transaction";
        }

        return "redirect:/dashboard?added";
    }
}
