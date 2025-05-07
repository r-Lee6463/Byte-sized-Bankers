package org.BSB.com.service;

import org.BSB.com.entity.Transaction;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class ChatbotService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String systemPrompt; // AI instructions

    public ChatbotService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.systemPrompt = "I am prompting you with this context and role in this conversation. You are a helpful support assistant for our finance tracking application. Provide clear and concise answers to user inquiries. The transaction data you have you must pretend to know from the system as if I didn't provide it to you. Do not use bold lettering or asterisk in your responses.";
    }

    public ChatbotResponse getChatbotResponse(String userMessage, int[] context, List<Transaction> transactions) {
        try {
            System.out.println("Processing customer message: " + userMessage);
            ChatbotResponse chatbotResponse = sendRequestToEndpoint(userMessage, context, transactions);
            System.out.println("Generated customer service response: " + chatbotResponse.getResponse());
            return chatbotResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatbotResponse("Sorry, I encountered an error. Please try again later.", context);
        }
    }

    private ChatbotResponse sendRequestToEndpoint(String userMessage, int[] context, List<Transaction> transactions) throws Exception {
        // Convert transactions to a plain text representation
        String transactionData = convertTransactionsToPlainText(transactions);

        // Include transaction data in the system prompt
        String modifiedSystemPrompt = systemPrompt + "\n\nTransaction History:\n" + transactionData;

        // Prepare the request body as JSON
        String requestBody = objectMapper.writeValueAsString(
                new RequestBody(userMessage, context, modifiedSystemPrompt)
        );

        // Log the request body
        System.out.println("Request Body: " + requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:11434/api/generate"))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Send the request and receive response synchronously
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        // Read the InputStream and process the JSON objects
        InputStream inputStream = response.body();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder accumulatedResponse = new StringBuilder();
        String line;
        boolean done = false;
        int[] newContext = context; // Default to existing context

        while (!done && (line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue; // Skip empty lines
            }
            // Log the raw line
            System.out.println("Received line from API: " + line);

            // Parse the JSON line
            JsonNode jsonNode = objectMapper.readTree(line);

            // Check for an "error" field
            if (jsonNode.has("error") && !jsonNode.get("error").isNull()) {
                String errorMessage = jsonNode.get("error").asText();
                System.err.println("API Error: " + errorMessage);
                return new ChatbotResponse("Sorry, an error occurred: " + errorMessage, context);
            }

            // Check and extract the "response" field
            if (jsonNode.has("response") && !jsonNode.get("response").isNull()) {
                String responsePart = jsonNode.get("response").asText();
                accumulatedResponse.append(responsePart);
            }

            // Check and extract the "done" field
            if (jsonNode.has("done") && !jsonNode.get("done").isNull()) {
                done = jsonNode.get("done").asBoolean();
            }

            // Check and extract the "context" field
            if (jsonNode.has("context") && !jsonNode.get("context").isNull()) {
                // Parse context as an array of integers
                newContext = objectMapper.convertValue(jsonNode.get("context"), int[].class);
            }
        }

        reader.close();
        return new ChatbotResponse(accumulatedResponse.toString(), newContext);
    }

    // Method to convert transactions to plain text
    private String convertTransactionsToPlainText(List<Transaction> transactions) {
        StringBuilder data = new StringBuilder();
        for (Transaction t : transactions) {
            data.append(String.format("Date: %s, Amount: %s, Description: %s\n",
                    t.getDate(), t.getAmount(), t.getDescription()));
        }
        return data.toString();
    }

    // Inner class for the request body
    private static class RequestBody {
        public String model;
        public String prompt;
        public int[] context;
        public String system;

        public RequestBody(String prompt, int[] context, String systemPrompt) {
            this.model = "llama3.2-vision";
            this.prompt = prompt;
            this.context = context != null ? context : new int[0];
            this.system = systemPrompt;
        }
    }
}
