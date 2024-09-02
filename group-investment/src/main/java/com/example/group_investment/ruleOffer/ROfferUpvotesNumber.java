package com.example.group_investment.ruleOffer;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("UPVOTE_NUMBER")
public class ROfferUpvotesNumber extends RuleOffer {
    private int tradeUpvotes;
}
