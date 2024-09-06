package com.example.stock_system.ranking;

import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.ranking.dto.RankingDto;
import com.example.stock_system.ranking.dto.SelectRankingResponse;
import com.example.stock_system.ranking.exception.RankingErrorCode;
import com.example.stock_system.ranking.exception.RankingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final AccountRepository accountRepository;
    public List<SelectRankingResponse> selectRanking(int teamId, int maxSeedMoney) {
        int minSeedMoney;

        if (maxSeedMoney==100)
            minSeedMoney = 0;
        else if (maxSeedMoney==1000)
            minSeedMoney = 101;
        else
            minSeedMoney = 1001;

        List<Ranking> rankings = rankingRepository.findBySeedMoneyAndOrderByEvluPflsRt(minSeedMoney, maxSeedMoney).orElseThrow(()->new RankingException(RankingErrorCode.RANKING_NOT_FOUNT));

        return rankings.stream()
                .map(ranking -> SelectRankingResponse.builder()
                        .category(ranking.getCategory())
                        .name(ranking.getAccount().getName())
                        .teamId(ranking.getTeamId())
                        .evluPflsRt(ranking.getEvluPflsRt())
                        .build())
                .collect(Collectors.toList());
    }
}
