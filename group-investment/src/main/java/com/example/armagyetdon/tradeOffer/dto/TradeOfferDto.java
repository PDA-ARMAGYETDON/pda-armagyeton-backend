package com.example.armagyetdon.tradeOffer.dto;

import com.example.armagyetdon.enums.TradeType;
import com.example.armagyetdon.member.Member;
import com.example.armagyetdon.tradeOffer.TradeOffer;

public class TradeOfferDto {
    private Member member;
    private TradeType tradeType;
    private String code;
    private int recentPrice;
    private int wantPrice;
    private int quantity;

}
