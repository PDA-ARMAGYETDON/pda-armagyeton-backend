package com.example.group_investment.ruleOfferVote.dto;

import com.example.group_investment.enums.Choice;
import com.example.group_investment.member.Member;
import com.example.group_investment.ruleOffer.RuleOffer;
import com.example.group_investment.ruleOfferVote.RuleOfferVote;
import lombok.Builder;

public class RuleOfferVoteDto {
    private Member member;
    private RuleOffer ruleOffer;
    private Choice choice;

    @Builder
    public RuleOfferVoteDto(Member member, RuleOffer ruleOffer, Choice choice) {
        this.member = member;
        this.ruleOffer = ruleOffer;
        this.choice = choice;
    }

    public RuleOfferVote toEntity() {
        return RuleOfferVote.builder()
                .member(this.member)
                .ruleOffer(this.ruleOffer)
                .choice(this.choice)
                .build();
    }
}

