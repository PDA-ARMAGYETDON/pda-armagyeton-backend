package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetROfferResponseUpvoteNumber implements GetROfferResponseType{

    @Enumerated(EnumType.STRING)
    RuleType type;
    int upvotes;
    int downvotes;
    int totalvotes;

    int tradeUpvotes;

    public GetROfferResponseUpvoteNumber(RuleType type, int upvotes, int downvotes, int totalVotes, int tradeUpvotes) {
        this.type = type;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalVotes;
        this.tradeUpvotes = tradeUpvotes;
    }

}
