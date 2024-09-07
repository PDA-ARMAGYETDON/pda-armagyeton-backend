package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.OfferStatus;
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
    int id;
    OfferStatus status;
    int upvotes;

    int downvotes;
    int totalvotes;

    double maxLossRt;
    double maxProfitRt;

    boolean isVote;

    public GetROfferResponseDisband(RuleType type, int id, OfferStatus status, int upvotes, int downvotes, int totalvotes, double maxLossRt, double maxProfitRt, boolean isVote) {
        this.type = type;
        this.id = id;
        this.status = status;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalvotes;
        this.maxLossRt = maxLossRt;
        this.maxProfitRt = maxProfitRt;
        this.isVote = isVote;
    }

}
