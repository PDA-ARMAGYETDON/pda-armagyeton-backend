package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.RuleOffer;

public interface CreateROfferRequest {
    RuleOffer toEntity(Rule rule, Member member, int totalMember);
    RuleType getType();
}
