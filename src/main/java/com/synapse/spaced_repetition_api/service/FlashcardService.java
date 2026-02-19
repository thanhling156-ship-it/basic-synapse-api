package com.synapse.spaced_repetition_api.service;


import com.synapse.spaced_repetition_api.dto.FlashcardDTO;
import com.synapse.spaced_repetition_api.entity.Flashcard;
import com.synapse.spaced_repetition_api.entity.User;
import com.synapse.spaced_repetition_api.repository.FlashcardRepository;
import com.synapse.spaced_repetition_api.repository.UserRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlashcardService {
    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmbeddingModel embeddingModel;


    @Transactional
    public String processStudyResponse(Long cardId, boolean isCorrect) {
        if (cardId == null) return "❌ LỖI: Thiếu ID thẻ.";

        // 1. Lấy username của phiên đăng nhập hiện tại
        String currentUsername = getCurrentUser().getUsername();

        // 2. Tìm thẻ dựa trên cả ID và quyền sở hữu để tránh lưu lệch flashcard vào ID khác
        return flashcardRepository.findByIdAndOwnerUsername(cardId, currentUsername)
                .map(card -> {
                    // Chỉ khi tìm thấy thẻ ĐÚNG CHỦ mới cho phép tính toán
                    calculateNextReviewDate(card, isCorrect);
                    flashcardRepository.save(card);
                    System.out.println("Level after updated : "+ card.getLevel()+" and context : "+card.getContext());
                    return "✅ Thành công: Đã cập nhật kết quả.";
                })
                .orElse("❌ LỖI: Bạn không có quyền truy cập thẻ này hoặc thẻ không tồn tại.");
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
            card.setLastTime(LocalDateTime.now());
            return;
        }

        if (!isCorrect) {
            // TRƯỜNG HỢP SAI: Reset và thoát sớm (Early Return)
            card.setLevel(0);
            card.setNextReviewDate(now.plusHours(6));
            card.setLastTime(LocalDateTime.now());
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
        card.setLastTime(LocalDateTime.now());
    }

    public String saveFlashcard(String content, List<Integer> intervals){
        User user =  getCurrentUser();

        Flashcard card = new Flashcard();
        card.setContext(content);
        card.setCustomIntervals(intervals);
        card.setNextReviewDate(LocalDateTime.now());
        card.setLastTime(null);
        card.setOwner(getCurrentUser());
        float[] vector = embeddingModel.embed(content);
        card.setEmbedding(vector);

        int flashcards_now = user.getFlashcards_now();
        int limitCards = user.getMaxFlashcards();

        int remainingSlots = limitCards - flashcards_now;

        // 2. Các tầng logic chặn (Guard Clauses)
        if (remainingSlots <= 0) {
            return "Đã hết vé! Hiện còn: 0 vé.";
        }

        if (remainingSlots <= 5) {
            flashcards_now+=1;
            user.setFlashcards_now(flashcards_now);
            flashcardRepository.save(card);
            userRepository.save(user);
            return "Cảnh báo: Đã hết số lần ưu tiên! (Chỉ còn " + remainingSlots + " vé). Thẻ vẫn được tạo.";
        }
        flashcards_now+=1;
        user.setFlashcards_now(flashcards_now);
        flashcardRepository.save(card);
        userRepository.save(user);
        return "Tạo thẻ thành công! Số vé còn lại: " + (remainingSlots - 1);
    }

    public List<FlashcardDTO> getExpiredCards() {
        String username = getCurrentUser().getUsername();
        List<Flashcard> cards = flashcardRepository.findByOwnerUsernameAndNextReviewDateBefore(
                username, LocalDateTime.now());

        // Chuyển đổi từ Entity sang DTO
        return cards.stream()
                .map(card -> FlashcardDTO.builder()
                        .content(card.getContext())
                        .intervals(card.getCustomIntervals())
                        .nextReviewDate(card.getNextReviewDate())
                        .build())
                .toList();
    }

    public List<Flashcard> searchSemantic(String userText) {
        // 1. Lấy "danh tính" từ phiên đăng nhập
        String currentUsername = getCurrentUser().getUsername();

        // 2. Biến câu hỏi thành Vector
        float[] queryVector = embeddingModel.embed(userText);

        // 3. Truy vấn: Chỉ tìm những thẻ của CHÍNH TÔI mà có nghĩa gần nhất
        return flashcardRepository.findNearest(queryVector, currentUsername);
    }

    private User getCurrentUser(){
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("❌ LỖI: Không tìm thấy user: " + username));
    }
}
