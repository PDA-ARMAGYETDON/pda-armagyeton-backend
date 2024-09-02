package com.example.stock_system.holdings.dto;

import com.example.stock_system.holdings.Holdings;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class HoldingsDto {

    private int hldgQty;      // 보유 수량
    private int pchsAmt;      // 매입 금액
    private int evluAmt;      // 평가 금액
    private int evluPfls;     // 평가 이익
    private double evluPflsRt; // 평가 이익률
    private String stockCode; // 종목 코드

    public HoldingsDto(Holdings holding, int evluAmt, int evluPfls, double evluPflsRt) {
        this.hldgQty = holding.getHldgQty();
        this.pchsAmt = holding.getPchsAmt();
        this.evluAmt = evluAmt;
        this.evluPfls = evluPfls;
        this.evluPflsRt = roundToTwoDecimalPlaces(evluPflsRt);
        this.stockCode = holding.getStockCode().getCode();
    }

    // DB에서 불러올 때 사용하는 생성자
    public HoldingsDto(Holdings holding) {
        this.hldgQty = holding.getHldgQty();
        this.pchsAmt = holding.getPchsAmt();
        this.evluAmt = holding.getEvluAmt();
        this.evluPfls = holding.getEvluPfls();
        this.evluPflsRt = holding.getEvluPflsRt();
        this.stockCode = holding.getStockCode().getCode();
    }

    // 소수점 2자리로 반올림하는 메소드
    private double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
