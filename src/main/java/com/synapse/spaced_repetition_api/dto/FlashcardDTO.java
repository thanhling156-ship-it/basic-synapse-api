package com.synapse.spaced_repetition_api.dto;

import java.util.List;

public class FlashcardDTO {
    private String content;

    private List<Integer> intervals;

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
}
