package com.synapse.spaced_repetition_api.repository;


import com.synapse.spaced_repetition_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user, trả về Optional để tránh lỗi NullPointerException
    Optional<User> findByUsername(String username);

    //Giúp trả về những trường tối thiểu, tiết kiệm băng thông
    @Query("SELECT new com.synapse.spaced_repetition_api.entity.User(u.username, u.role) FROM User u WHERE u.username = :username")
    Optional<User> findShadowUser(@Param("username") String username);

    boolean existsByUsername(String username);

    @Query("SELECT u.flashcards_now FROM User u WHERE u.username = :username")
    Integer findFlashcardsByUsername(@Param("username") String username);

    /*
    // Hàm update trực tiếp: Tăng số lượng thẻ lên 1 dựa vào username
    @Modifying
    @Transactional // Quan trọng: Phải có transaction để thực thi lệnh update
    @Query("UPDATE User u SET u.flashcardNow = u.flashcardNow + 1 WHERE u.username = :username")
    void incrementFlashcardCount(String username);

     */
}
