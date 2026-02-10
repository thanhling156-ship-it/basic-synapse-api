package com.synapse.spaced_repetition_api.service;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AiAgentService {

    private final ChatClient chatClient;

    // Spring sẽ tự động inject ChatClient.Builder vào đây
    public AiAgentService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String askAi(String message) {
        return chatClient.prompt()
                .user(message)
                .toolNames("searchFlashcard", "processStudyResult") // Đăng ký tool liên hoàn
                .call()
                .content();
    }
}