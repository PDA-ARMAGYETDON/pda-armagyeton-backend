package com.example.group_investment.tradeOffer.dto;

import com.example.group_investment.enums.TradeType;
import lombok.Getter;

@Getter
public class CreateTradeOfferRequest {
    private TradeType tradeType;
    private String code;
    private int recentPrice;
    private int wantPrice;
    private int quantity;
}
