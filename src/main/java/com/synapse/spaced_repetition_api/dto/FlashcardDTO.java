package com.synapse.spaced_repetition_api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.synapse.spaced_repetition_api.util.InlineArraySerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlashcardDTO {
    private String content;

    @JsonSerialize(using = InlineArraySerializer.class)
    private List<Integer> intervals;

    private LocalDateTime nextReviewDate;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Integer> intervals) {
        this.intervals = intervals;
    }

    public LocalDateTime getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(LocalDateTime nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }
}
