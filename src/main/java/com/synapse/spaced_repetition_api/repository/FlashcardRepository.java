package com.synapse.spaced_repetition_api.repository;

import com.synapse.spaced_repetition_api.entity.Flashcard;
import com.synapse.spaced_repetition_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    // Spring sẽ tự hiểu: Tìm các thẻ có NextReviewDate nhỏ hơn (Before) mốc thời gian truyền vào
    List<Flashcard> findByNextReviewDateBefore(LocalDateTime now);

    // Tìm kiếm thẻ theo nội dung, không phân biệt hoa thường
    List<Flashcard> findByContextContainingIgnoreCase(String context);
}
