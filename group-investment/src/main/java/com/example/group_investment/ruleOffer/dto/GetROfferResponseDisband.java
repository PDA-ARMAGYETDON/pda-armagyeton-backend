package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetROfferResponseDisband implements GetROfferResponseType {

    @Enumerated(EnumType.STRING)
    RuleType type;
    int upvotes;
    int downvotes;
    int totalvotes;

    double maxLossRt;
    double maxProfitRt;

    public GetROfferResponseDisband(RuleType type, int upvotes, int downvotes, int totalvotes, double maxLossRt, double maxProfitRt) {
        this.type = type;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalvotes;
        this.maxLossRt = maxLossRt;
        this.maxProfitRt = maxProfitRt;
    }

}
