package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.ROfferUpvotesNumber;
import com.example.group_investment.ruleOffer.RuleOffer;
import lombok.Getter;

@Getter
public class CreateROfferRequestUpvoteNumber implements CreateROfferRequest{
    String type;
    int tradeUpvotes;

    @Override
    public RuleOffer toEntity(Rule rule, Member member, int totalMember) {
        return ROfferUpvotesNumber.builder()
                .rule(rule)
                .member(member)
                .tradeUpvotes(tradeUpvotes)
                .build();
    }
}
