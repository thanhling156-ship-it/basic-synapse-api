package com.synapse.spaced_repetition_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.synapse.spaced_repetition_api.constant.UserRole;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role; // BASIC hoặc PREMIUM

    private int maxFlashcards = 5;

    public int getMaxFlashcards() {
        return maxFlashcards;
    }

    public void setMaxFlashcards(int maxFlashcards) {
        this.maxFlashcards = maxFlashcards;
    }

    @Column(unique = true, nullable = false)
    private String email;

    public User(UserRole role, String username, String password,String email) {
        this.role = role;
        this.username = username;
        this.password = password;
        this.email=email;
    }
    public User(){};

    // Trong file User.java của bạn
    public User(String username, UserRole role) {
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Flashcard> flashcards; // Chỉ cần thế này để quản lý thẻ

    public List<Flashcard> getCards() {
        return flashcards;
    }

    public void setCards(List<Flashcard> cards) {
        this.flashcards = cards;
    }
}
