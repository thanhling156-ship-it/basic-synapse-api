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

    // Thêm tham số username vào hàm tìm kiếm
    @Query(value = "SELECT * FROM cards " +
            "WHERE owner_username = :username " + // Khóa chặt "cánh cửa" dữ liệu
            "ORDER BY embedding <=> CAST(:queryVector AS vector) " +
            "LIMIT 3", nativeQuery = true)
    List<Flashcard> findNearest(
            @Param("queryVector") float[] queryVector,
            @Param("username") String username
    );

    // Spring Data JPA sẽ tự hiểu: tìm theo ID của Card và Username của User liên kết
    Optional<Flashcard> findByIdAndUserUsername(Long id, String username);
}
