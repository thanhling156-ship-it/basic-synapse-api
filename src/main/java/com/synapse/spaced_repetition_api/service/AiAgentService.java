package com.synapse.spaced_repetition_api.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiAgentService {

    private final ChatClient chatClient;

    // Spring AI sẽ tự động inject ChatClient.Builder liên kết với Gemini
    // dựa trên starter bạn đã cấu hình trong pom.xml
    public AiAgentService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String askAi(String message) {
        return chatClient.prompt()
                .system("""
                BẠN LÀ MỘT HỆ THỐNG XỬ LÝ DỮ LIỆU TỰ ĐỘNG CỦA SYNAPSE. 
                RÀO CẢN HÀNH VI:
                - KHÔNG giải thích nội dung kiến thức.
                - KHÔNG xin lỗi hay đặt câu hỏi ngược lại.
                - CHỈ phản hồi xác nhận '✅ Đã cập nhật xong' sau khi tất cả các hàm đã được thực thi thành công.
                """)
                .user(message)
                .toolNames("studyAndSync")
                // Đổi từ OllamaOptions sang GoogleAiChatOptions
                .call()
                .content();
    }
}