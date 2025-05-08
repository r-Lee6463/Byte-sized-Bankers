package org.BSB.com.service;

public class ChatbotResponse {
    private String response;
    private int[] context; // Changed from String to int[]

    public ChatbotResponse(String response, int[] context) {
        this.response = response;
        this.context = context;
    }

    public String getResponse() {
        return response;
    }

    public int[] getContext() {
        return context;
    }
}


