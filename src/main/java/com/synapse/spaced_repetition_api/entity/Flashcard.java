package com.synapse.spaced_repetition_api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cards")
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private LocalDateTime lastTime;
    private LocalDateTime nextReviewDate;
    private int level = 0;

    @Column(columnDefinition = "TEXT")
    private String context;

    // --- ĐOẠN THAY ĐỔI QUAN TRỌNG NHẤT ---
    @ManyToOne
    @JoinColumn(
            name = "owner_username",         // Tên cột trong bảng 'cards'
            referencedColumnName = "username" // Tên cột trong bảng 'users'
    )
    private User user;
    // ------------------------------------

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 768)
    @Column(name = "embedding", columnDefinition = "vector(768)")
    private float[] embedding;

    @Column(name = "intervals", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Integer> customIntervals = new ArrayList<>(List.of(1, 3, 7, 15, 30));

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public LocalDateTime getLastTime() { return lastTime; }
    public void setLastTime(LocalDateTime lastTime) { this.lastTime = lastTime; }

    public LocalDateTime getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDateTime nextReviewDate) { this.nextReviewDate = nextReviewDate; }

    public List<Integer> getCustomIntervals() { return customIntervals; }
    public void setCustomIntervals(List<Integer> customIntervals) { this.customIntervals = customIntervals; }

    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
}