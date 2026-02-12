package com.synapse.spaced_repetition_api.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.StringJoiner;

@Converter
public class VectorConverter implements AttributeConverter<float[], String> {

    @Override
    public String convertToDatabaseColumn(float[] attribute) {
        if (attribute == null || attribute.length == 0) return null;

        // Vì float[] không có Stream trực tiếp, ta dùng vòng lặp for cho nhanh và an toàn
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (float f : attribute) {
            joiner.add(String.valueOf(f));
        }
        return joiner.toString();
    }

    @Override
    public float[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || dbData.equals("[]")) return null;

        // Xóa bỏ dấu ngoặc vuông và cắt chuỗi
        String content = dbData.substring(1, dbData.length() - 1);
        String[] parts = content.split(",");
        float[] floats = new float[parts.length];

        for (int i = 0; i < parts.length; i++) {
            floats[i] = Float.parseFloat(parts[i].trim());
        }
        return floats;
    }
}