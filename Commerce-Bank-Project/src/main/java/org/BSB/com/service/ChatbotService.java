package org.BSB.com.service;

import org.BSB.com.entity.Transaction;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatbotService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String systemPrompt;

    public ChatbotService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.systemPrompt = ""
                + "I am prompting you with this context and role in this conversation. "
                + "You are a helpful support assistant for our finance tracking application. "
                + "Provide clear and concise answers to user inquiries. "
                + "The transaction data you have you must pretend to know from the system as if I didn't provide it to you. "
                + "Do not use bold lettering or asterisk in your responses. "
                + "If the user asks to add or delete anything (transactions or goals), reply with **only** a JSON object in one line containing:\n"
                + "• action — one of \"addTransaction\",\"createGoal\",\"deleteGoal\"\n"
                + "• the necessary parameters\n\n"
                + "For example:\n"
                + "{\"action\":\"addTransaction\",\"amount\":25.50,\"date\":\"2025-05-09\",\"description\":\"Lunch\",\"category\":\"Food\"}\n\n"
                + "Do **not** include any explanatory text. If they’re just asking questions, reply normally (plain text).";
    }

    /** Entry point for plain chat (no context, no transactions) */
    public ChatbotResponse chat(String userMessage) {
        return getChatbotResponse(userMessage, new int[0], Collections.emptyList());
    }

    /** Entry point for chat with existing context and transaction history */
    public ChatbotResponse chat(String userMessage, int[] context, List<Transaction> transactions) {
        return getChatbotResponse(userMessage, context, transactions);
    }

    private ChatbotResponse getChatbotResponse(String userMessage,
            int[] context,
            List<Transaction> transactions) {
        try {
            System.out.println("Processing customer message: " + userMessage);
            ChatbotResponse resp = sendRequestToEndpoint(userMessage, context, transactions);
            System.out.println("Generated customer service response: " + resp.getResponse());
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            // fallback: plain‐text error, preserve context
            return new ChatbotResponse(
                    "Sorry, I encountered an error. Please try again later.",
                    context);
        }
    }

    private ChatbotResponse sendRequestToEndpoint(String userMessage,
            int[] context,
            List<Transaction> transactions)
            throws Exception {

        // 1) Build the full system prompt with transaction history
        String txHistory = convertTransactionsToPlainText(transactions);
        String fullSystem = systemPrompt
                + "\n\nTransaction History:\n"
                + txHistory;

        // 2) Prepare the JSON request body
        RequestBody body = new RequestBody(userMessage, context, fullSystem);
        String requestJson = objectMapper.writeValueAsString(body);
        System.out.println("Request Body: " + requestJson);

        // 3) Send HTTP POST to your Ollama endpoint
        HttpRequest httpReq = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:11434/api/generate"))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<InputStream> httpResp = httpClient.send(
                httpReq,
                HttpResponse.BodyHandlers.ofInputStream());

        // 4) Stream‐read the response lines
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpResp.body()));
        StringBuilder acc = new StringBuilder();
        String line;
        boolean done = false;
        int[] newContext = context;

        while (!done && (line = reader.readLine()) != null) {
            if (line.isBlank())
                continue;
            System.out.println("Received line from API: " + line);

            JsonNode node = objectMapper.readTree(line);

            // error handling
            if (node.has("error") && !node.get("error").isNull()) {
                String err = node.get("error").asText();
                System.err.println("API Error: " + err);
                return new ChatbotResponse("Sorry, an error occurred: " + err, context);
            }

            // accumulate chat response
            if (node.has("response") && !node.get("response").isNull()) {
                acc.append(node.get("response").asText());
            }

            // check for completion
            if (node.has("done") && node.get("done").asBoolean()) {
                done = true;
            }

            // capture updated context
            if (node.has("context") && !node.get("context").isNull()) {
                newContext = objectMapper.convertValue(node.get("context"), int[].class);
            }
        }
        reader.close();

        String raw = acc.toString().trim();
        // 5) If raw looks like a single‐line JSON with an "action", extract it
        if (raw.startsWith("{") && raw.contains("\"action\"")) {
            Map<String, Object> map = objectMapper.readValue(
                    raw,
                    new TypeReference<Map<String, Object>>() {
                    });
            String action = (String) map.remove("action");
            Map<String, Object> params = map;
            // return with empty textual response
            return new ChatbotResponse("", newContext, action, params);
        }

        // 6) Otherwise, plain‐text reply
        return new ChatbotResponse(raw, newContext);
    }

    /** Flatten transaction list to simple text for prompting */
    private String convertTransactionsToPlainText(List<Transaction> txs) {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : txs) {
            sb.append(String.format(
                    "Date: %s, Amount: %s, Description: %s, Category: %s%n",
                    t.getDate(), t.getAmount(), t.getDescription(), t.getCategory()));
        }
        return sb.toString();
    }

    /** Internal structure matching Ollama’s API schema */
    private static class RequestBody {
        public final String model = "llama3.2-vision";
        public final String prompt;
        public final int[] context;
        public final String system;

        public RequestBody(String prompt, int[] context, String system) {
            this.prompt = prompt;
            this.context = context != null ? context : new int[0];
            this.system = system;
        }
    }
}
