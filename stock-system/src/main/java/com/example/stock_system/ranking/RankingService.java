package com.example.stock_system.ranking;

import com.example.stock_system.ranking.dto.SelectRankingResponse;
import com.example.stock_system.ranking.dto.SelectRankingWithTeamResponse;
import com.example.stock_system.ranking.exception.RankingErrorCode;
import com.example.stock_system.ranking.exception.RankingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    public SelectRankingWithTeamResponse selectRanking(int teamId, int maxSeedMoney) {
        int minSeedMoney;
        int teamRanking =-1;

        if (maxSeedMoney==1000000)
            minSeedMoney = 0;
        else if (maxSeedMoney==10000000)
            minSeedMoney = 1000001;
        else
            minSeedMoney = 10000001;

        List<Ranking> rankings = rankingRepository.findBySeedMoneyAndOrderByEvluPflsRt(minSeedMoney, maxSeedMoney).orElseThrow(()->new RankingException(RankingErrorCode.RANKING_NOT_FOUNT));

        for (int i = 0; i < rankings.size(); i++) {
            if (rankings.get(i).getTeamId() == teamId) {
                teamRanking = i;
                break;
            }
        }

        List<SelectRankingResponse> rankingResponses = rankings.stream()
                .map(ranking -> SelectRankingResponse.builder()
                        .category(ranking.getCategory())
                        .name(ranking.getAccount().getName())
                        .evluPflsRt(ranking.getEvluPflsRt())
                        .build())
                .collect(Collectors.toList());

        return SelectRankingWithTeamResponse.builder()
                .teamRanking(teamRanking)
                .rankings(rankingResponses)
                .build();
    }
}
