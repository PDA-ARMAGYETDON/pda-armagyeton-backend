package com.example.invest_references.dto;

import lombok.Getter;

@Getter
public class ShinhanData {

    private String stockName;

    private String rank;

    private String stockCode;

    public ShinhanData(String stockName, String rank, String stockCode) {
        this.stockName = stockName;
        this.rank = rank;
        this.stockCode = stockCode;
    }
}
