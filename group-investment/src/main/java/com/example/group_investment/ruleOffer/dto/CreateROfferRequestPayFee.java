package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RulePeriod;
import com.example.group_investment.enums.RuleType;
import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.ROfferPayFee;
import com.example.group_investment.ruleOffer.RuleOffer;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("PAY_FEE")
public class CreateROfferRequestPayFee implements CreateROfferRequest {
    String type;
    int depositAmt;
    RulePeriod period;
    LocalDate payDate;

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

    @Override
    public RuleType getType() {
        return RuleType.PAY_FEE;
    }
}
