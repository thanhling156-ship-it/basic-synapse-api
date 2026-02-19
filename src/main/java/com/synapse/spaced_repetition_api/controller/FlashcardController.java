package com.synapse.spaced_repetition_api.controller;

import com.synapse.spaced_repetition_api.dto.FlashcardDTO;
import com.synapse.spaced_repetition_api.entity.Flashcard;
import com.synapse.spaced_repetition_api.service.FlashcardService;
import com.synapse.spaced_repetition_api.dto.ApiResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardService service;

    @GetMapping("/due")
    public ResponseEntity<ApiResponseDTO<List<FlashcardDTO>>> getDueCards() {
        List<FlashcardDTO> cards = service.getExpiredCards();

        ApiResponseDTO<List<FlashcardDTO>> response = ApiResponseDTO.<List<FlashcardDTO>>builder() // <--- Thêm đoạn này
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách thẻ hết hạn thành công")
                .data(cards)
                .build();

        return ResponseEntity.ok(response);
    }

    /*Không dùng
    // 2. API để AI gửi kết quả học tập về (Đúng/Sai)
    @PostMapping("/{id}/study")
    public ResponseEntity<String> study(@PathVariable Long id, @RequestParam boolean isCorrect) {
        service.processStudyResponse(id, isCorrect);
        return ResponseEntity.ok("Đã cập nhật tiến độ!");
    }

     */

    // 3. API tạo thẻ mới
    @PostMapping
    public ResponseEntity<String> create(@RequestBody FlashcardDTO dto) {
        // Giả sử ông lấy username từ Session hoặc từ chính DTO
        String message = service.saveFlashcard(dto.getContent(), dto.getIntervals());

        // Nếu thông báo chứa chữ "hết vé" thì trả về 400 (Bad Request), ngược lại trả về 200 (OK)
        if (message.contains("Đã hết vé")) {
            return ResponseEntity.badRequest().body(message);
        }

        return ResponseEntity.ok(message);
    }
}