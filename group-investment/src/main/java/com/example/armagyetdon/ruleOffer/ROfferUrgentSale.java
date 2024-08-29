package com.example.armagyetdon.ruleOffer;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("URGENT_SALE")
public class ROfferUrgentSale extends RuleOffer {
    private int tradeUpvotes;
    private double prdyVrssRt;
}
