package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetROfferResponseUrgentSale implements GetROfferResponseType {

    @Enumerated(EnumType.STRING)
    RuleType type;
    int upvotes;
    int downvotes;
    int totalvotes;

    int tradeUpvotes;
    double prdyVrssRt;

    public GetROfferResponseUrgentSale(RuleType type, int upvotes, int downvotes, int totalvotes, int tradeUpvotes, int prdyVrssRt) {
        this.type = type;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalvotes;
        this.tradeUpvotes = tradeUpvotes;
        this.prdyVrssRt = prdyVrssRt;
    }

}
