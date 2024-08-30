package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.ROfferUrgentSale;
import com.example.group_investment.ruleOffer.RuleOffer;
import lombok.Getter;

@Getter
public class CreateROfferRequestUrgentSale implements CreateROfferRequest {
    String type;
    int tradeUpvotes;
    int prdyVrssRt;

    @Override
    public RuleOffer toEntity(Rule rule, Member member, int totalMember) {
        return ROfferUrgentSale.builder()
                .rule(rule)
                .member(member)
                .tradeUpvotes(tradeUpvotes)
                .prdyVrssRt(prdyVrssRt)
                .build();
    }
}
