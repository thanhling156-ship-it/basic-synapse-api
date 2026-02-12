package com.synapse.spaced_repetition_api.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapse.spaced_repetition_api.repository.FlashcardRepository;
import com.synapse.spaced_repetition_api.service.FlashcardService;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AiFunctionConfig {

    private final FlashcardService flashcardService;




    public AiFunctionConfig(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    // Công cụ giúp AI cập nhật tiến độ học tập
    @Bean
    @Description("Cập nhật kết quả học tập (Đúng/Sai) cho một thẻ flashcard dựa trên ID. Chỉ gọi hàm này KHI NGƯỜI DÙNG PHẢN HỒI về việc họ thuộc bài hay chưa.")
    public Function<StudyRequest, String> processStudyResult() {
        return request -> {
            // AI sẽ nhận được chuỗi String trả về từ Service (dù thành công hay lỗi)
            return flashcardService.processStudyResponse(request.cardId(), request.isCorrect());
        };
    }

    // Định nghĩa tham số đầu vào cho AI bóc tách
    public record StudyRequest(Long cardId, boolean isCorrect) {}

    @Bean
    @Description("Tìm kiếm ID flashcard. Tham số 'query' BẮT BUỘC phải là một chuỗi văn bản đơn giản (String), ví dụ: 'gause-jordan'. TUYỆT ĐỐI không gửi object vào đây.")
    public Function<SearchRequest, String> searchFlashcardBasic(FlashcardRepository repository) {
        return request -> {
            // Kiểm tra xem query có null không trước khi tìm kiếm
            if (request.searchTerm() == null) return "Lỗi: Tham số query không được trống.";

            var results = repository.findByContextContainingIgnoreCase(request.searchTerm());
            if (results.isEmpty()) return "Không tìm thấy thẻ.";

            return results.stream()
                    .map(c -> "ID: " + c.getId() + " - " + c.getContext())
                    .collect(Collectors.joining("\n"));
        };
    }

    @Bean
    @Description("Tìm kiếm kiến thức Đại số tuyến tính. Có thể gọi nhiều lần để liên kết các khái niệm.")
    public Function<SearchRequest, String> searchFlashcardPremium(FlashcardService service, ObjectMapper objectMapper) {
        return request -> {
            try {
                // Log để bạn theo dõi AI đang tìm gì trong chuỗi Multi-call
                System.out.println("🔍 AI đang gọi Tool với từ khóa: " + request.searchTerm());

                var results = service.searchSemantic(request.searchTerm());
                if (results.isEmpty()) return "{\"result\":\"NOT_FOUND\"}";

                // Lấy tối đa 2 kết quả để tránh làm đầy Context Window của gemini-2.5-flash-lite
                var data = results.stream().limit(2)
                        .map(f -> Map.of("id", f.getId(), "content", f.getContext()))
                        .toList();

                return objectMapper.writeValueAsString(data); // Đảm bảo JSON sạch 100%
            } catch (Exception e) {
                return "{\"error\":\"Lỗi xử lý dữ liệu\"}";
            }
        };
    }

    public record SearchRequest(
            @JsonProperty("search_term") String searchTerm
    ) {
        @JsonCreator
        public SearchRequest {}
    }

}
