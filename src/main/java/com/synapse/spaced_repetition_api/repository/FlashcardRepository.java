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

    // 1. Đúng: Tìm theo owner -> username
    List<Flashcard> findByOwnerUsernameAndNextReviewDateBefore(String username, LocalDateTime date);

    // 2. Tìm kiếm thẻ theo nội dung
    List<Flashcard> findByContextContainingIgnoreCase(String context);

    // 3. Native Query: Sử dụng đúng cột owner_username trong DB
    @Query(value = "SELECT * FROM cards " +
            "WHERE owner_username = :username " +
            "ORDER BY embedding <=> CAST(:queryVector AS vector) " +
            "LIMIT 3", nativeQuery = true)
    List<Flashcard> findNearest(
            @Param("queryVector") float[] queryVector,
            @Param("username") String username
    );

    // 4. SỬA TẠI ĐÂY: Đổi 'UserUsername' thành 'OwnerUsername'
    Optional<Flashcard> findByIdAndOwnerUsername(Long id, String username);
}
