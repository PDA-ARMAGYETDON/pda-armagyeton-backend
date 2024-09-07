package com.example.group_investment.tradeOffer.dto;

import com.example.group_investment.enums.TradeType;
import com.example.group_investment.member.Member;
import com.example.group_investment.team.Team;
import com.example.group_investment.tradeOffer.TradeOffer;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TradeOfferDto {
    private Member member;
    private Team team;
    private boolean isUrgent;
    private TradeType tradeType;
    private String code;
    private int recentPrice;
    private int wantPrice;
    private int quantity;

    public TradeOfferDto() {
    }

    @Builder
    public TradeOfferDto(Member member, Team team, boolean isUrgent, TradeType tradeType, String code, int recentPrice, int wantPrice, int quantity) {
        this.member = member;
        this.team = team;
        this.isUrgent = isUrgent;
        this.tradeType = tradeType;
        this.code = code;
        this.recentPrice = recentPrice;
        this.wantPrice = wantPrice;
        this.quantity = quantity;
    }

    public TradeOffer toEntity() {
        return TradeOffer.builder()
                .member(this.member)
                .team(this.team)
                .isUrgent(this.isUrgent)
                .tradeType(this.tradeType)
                .stockCode(this.code)
                .recentPrice(this.recentPrice)
                .wantPrice(this.wantPrice)
                .quantity(this.quantity)
                .build();
    }
}
