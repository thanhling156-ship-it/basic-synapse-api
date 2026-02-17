package com.synapse.spaced_repetition_api.dto;

import com.google.auto.value.AutoValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@AutoValue.Builder
public class ApiResponseDTO<T> {
    private int status;
    private String message;
    private T data; // Chứa kết quả trả về (có thể là String, Object hoặc List)
}
