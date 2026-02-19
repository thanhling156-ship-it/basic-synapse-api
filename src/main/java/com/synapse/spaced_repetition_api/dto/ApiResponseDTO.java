package com.synapse.spaced_repetition_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Để quyết định cấu trúc phản hồi chung của toàn bộ hệ thống (status, message, data)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//T tương ứng với List<FlashcardDTO>
public class ApiResponseDTO<T> {
    private int status;
    private String message;
    private T data; // Chứa kết quả trả về (có thể là String, Object hoặc List)
}
