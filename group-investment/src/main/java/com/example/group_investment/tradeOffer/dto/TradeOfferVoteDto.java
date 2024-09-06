package com.example.group_investment.tradeOffer.dto;

import com.example.group_investment.enums.Choice;
import com.example.group_investment.member.Member;
import com.example.group_investment.tradeOffer.TradeOffer;
import com.example.group_investment.tradeOffer.TradeOfferVote;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TradeOfferVoteDto {
    private Member member;
    private TradeOffer tradeOffer;
    private Choice choice;

    @Builder
    public TradeOfferVoteDto(Member member, TradeOffer tradeOffer, Choice choice) {
        this.member = member;
        this.tradeOffer = tradeOffer;
        this.choice = choice;
    }

    public TradeOfferVote toEntity() {
        return TradeOfferVote.builder()
                .member(this.member)
                .tradeOffer(this.tradeOffer)
                .choice(this.choice)
                .build();
    }
}
