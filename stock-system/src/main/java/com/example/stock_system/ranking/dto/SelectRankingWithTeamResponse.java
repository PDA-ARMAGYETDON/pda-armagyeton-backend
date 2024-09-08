package com.example.stock_system.ranking.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Builder(toBuilder = true)
@Getter
public class SelectRankingWithTeamResponse {
    private int teamRanking;
    private List<SelectRankingResponse> rankings;

    public SelectRankingWithTeamResponse(int teamRanking, List<SelectRankingResponse> rankings) {
        this.teamRanking = teamRanking;
        this.rankings = rankings;
    }
}
