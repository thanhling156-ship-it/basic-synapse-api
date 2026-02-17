package com.synapse.spaced_repetition_api.exception;

// File: CardLimitExceededException.java
public class CardLimitExceededException extends RuntimeException {

    // Constructor nhận vào một message thông báo lỗi
    public CardLimitExceededException(String message) {
        super(message);
    }
}