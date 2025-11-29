package com.eduforum.api.domain.active.dto.discussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationStatsResponse {

    private Long seminarRoomId;
    private Integer totalParticipants;
    private Integer totalSpeakingSeconds;
    private List<UserStats> userStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Long userId;
        private String userName;
        private Integer speakingCount;
        private Integer totalSpeakingSeconds;
        private Double averageSpeakingSeconds;
    }
}
