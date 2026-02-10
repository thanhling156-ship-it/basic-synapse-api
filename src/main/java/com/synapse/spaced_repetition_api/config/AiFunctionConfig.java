package com.synapse.spaced_repetition_api.config;

import com.synapse.spaced_repetition_api.repository.FlashcardRepository;
import com.synapse.spaced_repetition_api.service.FlashcardService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
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
    @Description("Cập nhật kết quả học tập. Tham số 'cardId' PHẢI là một số nguyên duy nhất (ví dụ: 3), KHÔNG được bọc trong object.")
    public Function<StudyRequest, String> processStudyResult() {

        return request -> {
            flashcardService.processStudyResponse(request.cardId(), request.isCorrect());
            return "Đã cập nhật kết quả cho thẻ " + request.cardId();
        };
    }

    // Định nghĩa tham số đầu vào cho AI bóc tách
    public record StudyRequest(Long cardId, boolean isCorrect) {}

    @Bean
    @Description("Tìm kiếm ID flashcard. Tham số 'query' BẮT BUỘC phải là một chuỗi văn bản đơn giản (String), ví dụ: 'gause-jordan'. TUYỆT ĐỐI không gửi object vào đây.")
    public Function<SearchRequest, String> searchFlashcard(FlashcardRepository repository) {
        return request -> {
            // Kiểm tra xem query có null không trước khi tìm kiếm
            if (request.query() == null) return "Lỗi: Tham số query không được trống.";

            var results = repository.findByContextContainingIgnoreCase(request.query());
            if (results.isEmpty()) return "Không tìm thấy thẻ.";

            return results.stream()
                    .map(c -> "ID: " + c.getId() + " - " + c.getContext())
                    .collect(Collectors.joining("\n"));
        };
    }
    public record SearchRequest(String query) {}
}
