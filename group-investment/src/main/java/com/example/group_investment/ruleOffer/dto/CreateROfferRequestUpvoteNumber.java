package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.ROfferUpvotesNumber;
import com.example.group_investment.ruleOffer.RuleOffer;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("UPVOTE_NUMBER")
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

    @Override
    public RuleType getType() {
        return RuleType.UPVOTE_NUMBER;
    }
}
