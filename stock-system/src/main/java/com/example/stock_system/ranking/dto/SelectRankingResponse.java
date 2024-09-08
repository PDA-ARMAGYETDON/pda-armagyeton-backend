package com.example.stock_system.ranking.dto;

import com.example.stock_system.enums.Category;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class SelectRankingResponse {
    private Category category;
    private String name;
    private double evluPflsRt;

    public SelectRankingResponse(Category category, String name, double evluPflsRt) {
        this.category = category;
        this.name = name;
        this.evluPflsRt = evluPflsRt;
    }
}
