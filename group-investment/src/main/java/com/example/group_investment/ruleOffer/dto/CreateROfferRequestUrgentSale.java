package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.ROfferUrgentSale;
import com.example.group_investment.ruleOffer.RuleOffer;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("URGENT_SALE")
public class CreateROfferRequestUrgentSale implements CreateROfferRequest {
    String type;
    int tradeUpvotes;
    double prdyVrssRt;

    @Override
    public RuleOffer toEntity(Rule rule, Member member, int totalMember) {
        return ROfferUrgentSale.builder()
                .rule(rule)
                .member(member)
                .tradeUpvotes(tradeUpvotes)
                .prdyVrssRt(prdyVrssRt)
                .build();
    }

    @Override
    public RuleType getType() {
        return RuleType.URGENT_SALE;
    }
}
