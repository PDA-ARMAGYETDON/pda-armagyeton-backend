package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RulePeriod;
import com.example.group_investment.enums.RuleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GetROfferResponsePayFee implements GetROfferResponseType {

    @Enumerated(EnumType.STRING)
    RuleType type;
    int upvotes;
    int downvotes;
    int totalvotes;

    int depositAmt;
    RulePeriod period;
    LocalDate payDate;

    public GetROfferResponsePayFee(RuleType type, int upvotes, int downvotes, int totalvotes, int depositAmt, RulePeriod period, LocalDate payDate) {
        this.type = type;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalvotes;
        this.depositAmt = depositAmt;
        this.period = period;
        this.payDate = payDate;
    }

}
