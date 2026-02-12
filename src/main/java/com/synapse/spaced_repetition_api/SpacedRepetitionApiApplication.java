package com.synapse.spaced_repetition_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		// Chỉ tắt phần Connection tự động vì nó đòi Project-ID vô lý
		org.springframework.ai.model.google.genai.autoconfigure.embedding.GoogleGenAiEmbeddingConnectionAutoConfiguration.class
})
public class SpacedRepetitionApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpacedRepetitionApiApplication.class, args);
	}
}
