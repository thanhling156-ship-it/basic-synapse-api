package com.synapse.spaced_repetition_api.constant;

public enum UserRole {
    BASIC(5), // Gói cơ bản: 5 flashcards
    VIP (1000),// Gói trung bình: 1000 flashcards
    PREMIUM(9999); // Gói Premium: Coi như không giới hạn

    private final int maxFlashcards;

    // Constructor của Enum
    UserRole(int maxFlashcards) {
        this.maxFlashcards = maxFlashcards;
    }

    //trả về số lượng card tùy theo ROLE
    public int getMaxFlashcards() {
        return maxFlashcards;
    }
}
