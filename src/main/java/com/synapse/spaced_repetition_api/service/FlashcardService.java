package com.synapse.spaced_repetition_api.service;


import com.synapse.spaced_repetition_api.entity.Flashcard;
import com.synapse.spaced_repetition_api.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlashcardService {
    @Autowired
    private FlashcardRepository repository;


    @Transactional
    public void processStudyResponse(Long cardId,boolean isCorrect){
        Flashcard card = repository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ với ID: " + cardId));
        List<Integer> intervals = card.getCustomIntervals();
        calculateNextReviewDate(card,isCorrect);
        System.out.println("Card ID: " + cardId + " | New Level: " + card.getLevel());
        repository.save(card);
    }

    // 4. Hàm Helper tính toán (Logic lõi)
    // Input 1 đối tượng + kết quả => trả về giờ
    private void calculateNextReviewDate(Flashcard card, boolean isCorrect) {
        LocalDateTime now = LocalDateTime.now();
        List<Integer> intervals = card.getCustomIntervals();
        int currentLevel = card.getLevel();

        // Check null ngay lập tức để tránh lỗi size()
        if (intervals == null || intervals.isEmpty()) {
            // Có thể log một cảnh báo ở đây
            // Trả về giá trị an toàn: học lại sau 1 ngày
            card.setNextReviewDate(LocalDateTime.now().plusDays(1));
            return;
        }

        if (!isCorrect) {
            // TRƯỜNG HỢP SAI: Reset và thoát sớm (Early Return)
            card.setLevel(0);
            card.setNextReviewDate(now.plusHours(6));
            return; // Dừng hàm ở đây, không chạy xuống dưới nữa
        }

        // TRƯỜNG HỢP ĐÚNG
        if (currentLevel < intervals.size()) {
            // Lấy số ngày dựa trên level hiện tại trước khi tăng level
            int daysToAdd = intervals.get(currentLevel);
            card.setNextReviewDate(now.plusDays(daysToAdd));

            // Sau đó mới tăng level cho lần học kế tiếp
            card.setLevel(currentLevel + 1);
        } else {
            // Đã tốt nghiệp
            card.setNextReviewDate(now.plusYears(1));
        }
    }

    public void saveFlashcard(String content, List<Integer> intervals){
        Flashcard card = new Flashcard();
        card.setContext(content);
        card.setCustomIntervals(intervals);
        card.setNextReviewDate(LocalDateTime.now());
        card.setLastTime(null);

        repository.save(card);
    }

    public List<Flashcard> getDueFlashcards() {
        return repository.findByNextReviewDateBefore(LocalDateTime.now());
    }
}
