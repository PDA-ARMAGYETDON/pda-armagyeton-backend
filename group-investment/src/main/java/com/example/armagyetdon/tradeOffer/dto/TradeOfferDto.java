package com.example.armagyetdon.tradeOffer.dto;

import com.example.armagyetdon.enums.TradeType;
import com.example.armagyetdon.member.Member;
import com.example.armagyetdon.tradeOffer.TradeOffer;
import lombok.Builder;

public class TradeOfferDto {
    private Member member;
    private TradeType tradeType;
    private String code;
    private int recentPrice;
    private int wantPrice;
    private int quantity;

    @Builder
    public TradeOfferDto(Member member, TradeType tradeType, String code, int recentPrice, int wantPrice, int quantity) {
        this.member = member;
        this.tradeType = tradeType;
        this.code = code;
        this.recentPrice = recentPrice;
        this.wantPrice = wantPrice;
        this.quantity = quantity;
    }

    public TradeOffer toEntity() {
        return TradeOffer.builder()
                .member(this.member)
                .tradeType(this.tradeType)
                .stockCode(this.code)
                .recentPrice(this.recentPrice)
                .wantPrice(this.wantPrice)
                .quantity(this.quantity)
                .build();
    }
}
