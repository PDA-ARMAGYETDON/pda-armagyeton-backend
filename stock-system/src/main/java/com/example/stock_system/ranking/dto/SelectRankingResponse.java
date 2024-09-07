package com.example.stock_system.ranking.dto;

import com.example.stock_system.enums.Category;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class SelectRankingResponse {
    private Category category;
    private String name;
    private int seedMoney;
    private double evluPflsRt;

    public SelectRankingResponse(Category category, String name, int seedMoney, double evluPflsRt) {
        this.category = category;
        this.name = name;
        this.seedMoney = seedMoney;
        this.evluPflsRt = evluPflsRt;
    }
}
