package com.example.stock_system.holdings.dto;

import lombok.Getter;

@Getter
public class SaveClosingPrice {
    private final int evluAmt;
    private final int evluPfls;
    private final double evluPflsRt;

    public SaveClosingPrice(int evluAmt, int evluPfls, double evluPflsRt) {
        this.evluAmt = evluAmt;
        this.evluPfls = evluPfls;
        this.evluPflsRt = evluPflsRt;
    }
}
