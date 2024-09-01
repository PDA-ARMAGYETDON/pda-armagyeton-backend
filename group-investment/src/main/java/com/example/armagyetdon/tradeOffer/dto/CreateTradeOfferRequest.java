package com.example.armagyetdon.tradeOffer.dto;

import com.example.armagyetdon.enums.TradeType;
import lombok.Getter;

@Getter
public class CreateTradeOfferRequest {
    private TradeType tradeType;
    private String code;
    private int recentPrice;
    private int wantPrice;
    private int quantity;
}
