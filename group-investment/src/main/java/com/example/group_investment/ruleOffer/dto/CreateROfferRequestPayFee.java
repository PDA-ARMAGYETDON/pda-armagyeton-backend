package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.ROfferPayFee;
import com.example.group_investment.ruleOffer.RuleOffer;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateROfferRequestPayFee implements CreateROfferRequest {
    String type;
    int depositAmt;
    int period;
    LocalDateTime payDate;

    @Override
    public RuleOffer toEntity(Rule rule, Member member, int totalMember) {
        return ROfferPayFee.builder()
                .rule(rule)
                .member(member)
                .depositAmt(depositAmt)
                .period(period)
                .payDate(payDate)
                .build();
    }
}
