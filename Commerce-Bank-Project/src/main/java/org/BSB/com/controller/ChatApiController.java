package org.BSB.com.controller;

import org.BSB.com.service.ChatbotResponse;
import org.BSB.com.service.ChatbotService;
import org.BSB.com.service.TransactionService;
import org.BSB.com.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/ollama")
public class ChatApiController {

    private final ChatbotService chatbot;
    private final TransactionService txSvc;
    private final GoalService goalSvc;

    @Autowired
    public ChatApiController(ChatbotService chatbot,
                             TransactionService txSvc,
                             GoalService goalSvc) {
        this.chatbot = chatbot;
        this.txSvc   = txSvc;
        this.goalSvc = goalSvc;
    }

    @PostMapping(path = "/chat",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatbotResponse chatApi(@RequestBody Map<String, String> payload,
                                   Principal principal) {

        String userMsg = payload.get("message");
        ChatbotResponse botResp = chatbot.chat(userMsg);

        // extract the parsed action + parameters from the LLM rep
        String act               = botResp.getAction();
        Map<String, Object> params = botResp.getParameters();

        if (act != null && params != null) {
            switch (act) {

                case "addTransaction":
                    // pull each parameter out of the map (and avoid NPEs by checking)
                    if (params.containsKey("amount")
                     && params.containsKey("date")
                     && params.containsKey("description")
                     && params.containsKey("category")) {

                        BigDecimal amt = new BigDecimal(
                            params.get("amount").toString()
                        );
                        LocalDate date = LocalDate.parse(
                            params.get("date").toString()
                        );
                        String desc = params.get("description").toString();
                        String cat  = params.get("category").toString();

                        txSvc.addTransaction(
                            principal.getName(),
                            amt, date, desc, cat
                        );

                        return new ChatbotResponse(
                            "✅ Added $" + amt + " on " + date + ".",
                            botResp.getContext()
                        );
                    }
                    break;

                case "createGoal":
                    if (params.containsKey("name")
                     && params.containsKey("limit")) {

                        String name = params.get("name").toString();
                        BigDecimal lim = new BigDecimal(
                            params.get("limit").toString()
                        );

                        goalSvc.createGoal(
                            principal.getName(),
                            name, lim
                        );

                        return new ChatbotResponse(
                            "✅ Created goal “" + name + "” at limit " + lim + ".",
                            botResp.getContext()
                        );
                    }
                    break;

                case "deleteGoal":
                    if (params.containsKey("id")) {
                        Long goalId = Long.valueOf(
                            params.get("id").toString()
                        );
                        goalSvc.deleteGoal(
                            principal.getName(),
                            goalId
                        );
                        return new ChatbotResponse(
                            "✅ Deleted goal #" + goalId + ".",
                            botResp.getContext()
                        );
                    }
                    break;

                default:
                    // unknown action — fall through to returning text
            }
        }

        // no actionable JSON (or map was null) → bubble up the LLM’s plain‐text reply
        return botResp;
    }
}
