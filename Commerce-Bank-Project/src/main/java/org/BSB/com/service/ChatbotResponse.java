package org.BSB.com.service;

import java.util.Map;

public class ChatbotResponse {

    private String response;
    private int[] context;
    private String action;
    private Map<String, Object> parameters;

    // Jackson needs a no-arg ctor
    public ChatbotResponse() {
    }

    /** “normal” text response only */
    public ChatbotResponse(String response) {
        this(response, new int[0]);
    }

    /** full constructor */
    public ChatbotResponse(String response, int[] context) {
        this.response = response;
        this.context = context != null ? context : new int[0];
    }

    // new all-args ctor
    public ChatbotResponse(String response,
            int[] context,
            String action,
            Map<String, Object> parameters) {
        this.response = response;
        this.context = context;
        this.action = action;
        this.parameters = parameters;
    }

    // Getters & Setters

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int[] getContext() {
        return context;
    }

    public void setContext(int[] context) {
        this.context = context;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
