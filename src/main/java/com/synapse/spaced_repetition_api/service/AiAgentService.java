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
                
                QUY TRÌNH XỬ LÝ (NGHIÊM CẤM LÀM SAI):
                1. Khi nhận thông điệp 'hoàn thành/xong/thuộc', bạn PHẢI gọi 'searchFlashcardPremium' để lấy ID.
                2. Sau khi có ID từ kết quả tìm kiếm, bạn PHẢI gọi tiếp 'processStudyResult' với cardId đó và isCorrect=true.
                3. Nếu 'processStudyResult' trả về thông báo LỖI (không tìm thấy ID), bạn PHẢI tự động gọi lại 'searchFlashcardPremium' một lần nữa để lấy ID mới nhất và thực hiện lại bước 2.
                
                RÀO CẢN HÀNH VI:
                - KHÔNG giải thích nội dung kiến thức.
                - KHÔNG xin lỗi hay đặt câu hỏi ngược lại.
                - CHỈ phản hồi xác nhận '✅ Đã cập nhật xong' sau khi tất cả các hàm đã được thực thi thành công.
                """)
                .user(message)
                .toolNames("searchFlashcardPremium", "processStudyResult")
                // Đổi từ OllamaOptions sang GoogleAiChatOptions
                .call()
                .content();
    }
}