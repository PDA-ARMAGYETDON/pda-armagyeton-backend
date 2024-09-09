package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.OfferStatus;
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
    int id;
    OfferStatus status;
    int upvotes;
    int downvotes;
    int totalvotes;

    int tradeUpvotes;
    boolean isVote;
    String name;

    public GetROfferResponseUpvoteNumber(RuleType type, int id, OfferStatus status, int upvotes, int downvotes, int totalVotes, int tradeUpvotes, boolean isVote, String name) {
        this.type = type;
        this.id = id;
        this.status = status;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalVotes;
        this.tradeUpvotes = tradeUpvotes;
        this.isVote = isVote;
        this.name = name;
    }

}
