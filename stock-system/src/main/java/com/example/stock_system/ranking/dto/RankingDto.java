package com.example.stock_system.ranking.dto;

import com.example.stock_system.account.Account;
import com.example.stock_system.enums.Category;
import com.example.stock_system.ranking.Ranking;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class RankingDto {
    private Category category;
    private Account account;
    private int teamId;
    private int seedMoney;
    private double evluPflsRt;

    @Builder
    public RankingDto(Category category, Account account, int teamId, int seedMoney, double evluPflsRt) {
        this.category = category;
        this.account = account;
        this.teamId = teamId;
        this.seedMoney = seedMoney;
        this.evluPflsRt = evluPflsRt;
    }

    public Ranking toEntity() {
        return Ranking.builder()
                .account(this.account)
                .category(this.category)
                .seedMoney(this.seedMoney)
                .teamId(this.teamId)
                .evluPflsRt(this.evluPflsRt).build();
    }
}
