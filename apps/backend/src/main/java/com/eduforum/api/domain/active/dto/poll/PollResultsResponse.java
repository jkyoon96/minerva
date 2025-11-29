package com.eduforum.api.domain.active.dto.poll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollResultsResponse {

    private Long pollId;
    private String question;
    private Long totalResponses;
    private List<OptionResult> optionResults;
    private List<String> textResponses;
    private Map<String, Integer> wordCloudData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResult {
        private Long optionId;
        private String text;
        private Long count;
        private Double percentage;
    }
}
