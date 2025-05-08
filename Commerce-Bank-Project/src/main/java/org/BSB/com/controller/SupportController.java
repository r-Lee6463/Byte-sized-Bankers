package org.BSB.com.controller;

import org.BSB.com.dto.ChatMessageDto;
import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;
import org.BSB.com.repository.TransactionRepository;
import org.BSB.com.service.ChatbotResponse;
import org.BSB.com.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class SupportController {

    private final ChatbotService chatbotService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public SupportController(ChatbotService chatbotService, TransactionRepository transactionRepository) {
        this.chatbotService = chatbotService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/support")
    public String showSupportPage(HttpSession session) {
        // Clear context when starting a new chat session, if desired
        session.removeAttribute("context");
        return "support";  // View name for the support chat page
    }

    @PostMapping("/support/chat")
    public ResponseEntity<String> getChatbotResponse(@RequestBody ChatMessageDto chatMessage, HttpSession session) {
        // Retrieve context from session
        int[] context = (int[]) session.getAttribute("context");

        // Retrieve the current user from the session
        User currentUser = (User) session.getAttribute("currentUser");

        // Ensure currentUser is not null
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        // Retrieve all transactions for the current user
        List<Transaction> transactions = transactionRepository.findByUser(currentUser);

        // Get the chatbot's response along with the updated context
        ChatbotResponse chatbotResponse = chatbotService.getChatbotResponse(
                chatMessage.getMessage(), context, transactions
        );

        // Store the updated context back into the session
        session.setAttribute("context", chatbotResponse.getContext());

        // Return only the chatbot's response to the frontend
        return ResponseEntity.ok(chatbotResponse.getResponse());
    }
}
