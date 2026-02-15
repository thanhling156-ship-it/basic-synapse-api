package com.synapse.spaced_repetition_api.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final FlashcardRepository flashcardRepository;
    private final ObjectMapper objectMapper;

    // Tiêm tất cả "đồ nghề" ở đây một lần duy nhất
    public AiFunctionConfig(FlashcardService flashcardService,
                            FlashcardRepository flashcardRepository,
                            ObjectMapper objectMapper) {
        this.flashcardService = flashcardService;
        this.flashcardRepository = flashcardRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * HÀM 1: BASIC SEARCH (Giữ nguyên theo yêu cầu)
     * Dùng để tìm kiếm chính xác theo từ khóa (Keyword match)
     */
    @Bean
    @Description("Tìm kiếm Flashcard theo từ khóa chính xác. Dùng khi người dùng muốn liệt kê các thẻ chứa một từ cụ thể.")
    public Function<SearchRequest, String> searchFlashcardBasic() {
        return request -> {
            if (request.searchTerm() == null) return "❌ Lỗi: Từ khóa không được để trống.";

            var results = flashcardRepository.findByContextContainingIgnoreCase(request.searchTerm());
            if (results.isEmpty()) return "Không tìm thấy thẻ nào khớp từ khóa.";

            return results.stream()
                    .map(c -> "ID: " + c.getId() + " - " + c.getContext())
                    .collect(Collectors.joining("\n"));
        };
    }

    /**
     * HÀM 2: MASTER FUNCTION (Gộp Search Premium + Update Result)
     * Đây là "cú đấm thép" giúp AI thực hiện cả 2 việc: Tìm thẻ và Cập nhật chỉ trong 1 lần gọi.
     */
    @Bean
    @Description("""
    Cập nhật tiến độ học tập cho TỪNG kiến thức cụ thể. 
    LƯU Ý QUAN TRỌNG: 
    - Nếu người dùng nhắc đến nhiều kiến thức cùng lúc (ví dụ: 'Tôi thuộc bài A và B', 'Tôi đã hiểu sự khác nhau giữa A và B'), 
      bạn BẮT BUỘC phải gọi hàm này NHIỀU LẦN: một lần cho 'A' và một lần cho 'B'. 
    - Tuyệt đối không gộp chung nhiều kiến thức vào một lần gọi.
    - Tham số 'context' phải là tên kiến thức ngắn gọn, súc tích.
    - Những từ như 'nắm vững', 'hiểu bài', ... thì đều là đúng
    """)
    public Function<StudyByContextRequest, String> studyAndSync() {
        return request -> {
            try {
                System.out.println("🔍 AI Architect: Đang đồng bộ tiến độ cho nội dung: " + request.context());

                // 1. Tự động tìm thẻ khớp nhất (Semantic Search)
                var results = flashcardService.searchSemantic(request.context());
                if (results.isEmpty()) return "❌ Không tìm thấy thẻ nào liên quan đến '" + request.context() + "' để cập nhật.";

                // 2. Lấy thẻ đứng đầu (khớp nhất) để update
                Long targetId = results.get(0).getId();

                System.out.println("ĐÚNG HAY SAI : "+ request.isCorrect());
                // 3. Gọi service để "hàn" dữ liệu vào Postgres
                return flashcardService.processStudyResponse(targetId, request.isCorrect());
            } catch (Exception e) {
                return "❌ Lỗi hệ thống: " + e.getMessage();
            }
        };
    }

    // --- CÁC DTO (RECORDS) GỌN GÀNG ---

    public record SearchRequest(
            @JsonProperty("search_term") String searchTerm
    ) {
        @JsonCreator public SearchRequest {}
    }

    public record StudyByContextRequest(
            @JsonProperty("context") String context,
            @JsonProperty("is_correct") boolean isCorrect
    ) {
        @JsonCreator public StudyByContextRequest {}
    }
}