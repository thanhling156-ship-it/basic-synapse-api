package com.synapse.spaced_repetition_api.controller;

import com.synapse.spaced_repetition_api.dto.FlashcardDTO;
import com.synapse.spaced_repetition_api.entity.Flashcard;
import com.synapse.spaced_repetition_api.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardService service;

    // 1. API lấy danh sách thẻ cần học hôm nay
    @GetMapping("/due")
    public ResponseEntity<List<Flashcard>> getDueCards() {
        return ResponseEntity.ok(service.getDueFlashcards());
    }

    // 2. API để AI gửi kết quả học tập về (Đúng/Sai)
    @PostMapping("/{id}/study")
    public ResponseEntity<String> study(@PathVariable Long id, @RequestParam boolean isCorrect) {
        service.processStudyResponse(id, isCorrect);
        return ResponseEntity.ok("Đã cập nhật tiến độ!");
    }

    // 3. API tạo thẻ mới
    @PostMapping
    public ResponseEntity<String> create(@RequestBody FlashcardDTO dto) {
        service.saveFlashcard(dto.getContent(), dto.getIntervals());
        return ResponseEntity.ok("Tạo thẻ thành công!");
    }
}