package com.synapse.spaced_repetition_api.repository;


import com.synapse.spaced_repetition_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user, trả về Optional để tránh lỗi NullPointerException
    Optional<User> findByUsername(String username);

    //Giúp trả về những trường tối thiểu, tiết kiệm băng thông
    @Query("SELECT new com.synapse.spaced_repetition_api.entity.User(u.username, u.role) FROM User u WHERE u.username = :username")
    Optional<User> findShadowUser(@Param("username") String username);

    boolean existsByUsername(String username);
}
