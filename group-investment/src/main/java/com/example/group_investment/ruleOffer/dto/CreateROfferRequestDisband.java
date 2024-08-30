package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.ROfferDisband;
import com.example.group_investment.ruleOffer.RuleOffer;
import lombok.Getter;

@Getter
public class CreateROfferRequestDisband implements CreateROfferRequest {
    String type;
    double maxLossRt;
    double maxProfitRt;

    @Override
    public RuleOffer toEntity(Rule rule, Member member, int totalMember) {
        return ROfferDisband.builder()
                .rule(rule)
                .member(member)
                .maxLossRt(maxLossRt)
                .maxProfitRt(maxProfitRt)
                .build();
    }
}
