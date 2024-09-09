package com.example.stock_system.ranking;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.account.Account;
import com.example.stock_system.account.TeamAccount;
import com.example.stock_system.account.TeamAccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.enums.Category;
import com.example.stock_system.ranking.dto.SelectRankingResponse;
import com.example.stock_system.ranking.dto.SelectRankingWithTeamResponse;
import com.example.stock_system.ranking.exception.RankingErrorCode;
import com.example.stock_system.ranking.exception.RankingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    @Value("${ag.url}")
    private String AG_URL;

    private final RankingRepository rankingRepository;
    private final TeamAccountRepository teamAccountRepository;
    private final RestTemplate restTemplate;

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

    public void registRanking(Account savedAccount){
        TeamAccount teamAccount = teamAccountRepository.findByAccount(savedAccount).orElseThrow(()-> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        int teamId = teamAccount.getTeamId();

        ObjectMapper objectMapper = new ObjectMapper();

        String url = AG_URL+"/api/group/backend/team-category?teamId="+teamId;

        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);
        Category teamCategory = objectMapper.convertValue(response.getBody().getData(), new TypeReference<Category>() {});

        rankingRepository.save(new Ranking(teamCategory,savedAccount,teamId,savedAccount.getDeposit(),savedAccount.getTotalEvluPflsRt()));
    }
}
