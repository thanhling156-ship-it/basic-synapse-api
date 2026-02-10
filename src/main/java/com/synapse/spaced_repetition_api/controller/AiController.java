package com.synapse.spaced_repetition_api.controller;

import com.synapse.spaced_repetition_api.service.AiAgentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiAgentService aiAgentService;

    public AiController(AiAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        // Gọi AI xử lý tin nhắn của bạn
        return aiAgentService.askAi(message);
    }
}