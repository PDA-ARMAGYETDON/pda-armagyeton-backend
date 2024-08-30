package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.member.Member;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.ruleOffer.RuleOffer;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateROfferRequestPayFee.class, name = "PAY_FEE"),
        @JsonSubTypes.Type(value = CreateROfferRequestDisband.class, name = "DISBAND"),
        @JsonSubTypes.Type(value = CreateROfferRequestUrgentSale.class, name = "URGENT_SALE"),
        @JsonSubTypes.Type(value = CreateROfferRequestUpvoteNumber.class, name = "UPVOTE_NUMBER"),
})
public interface CreateROfferRequest {
    RuleOffer toEntity(Rule rule, Member member, int totalMember);
    RuleType getType();
}
