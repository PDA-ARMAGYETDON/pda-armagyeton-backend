package com.example.group_investment.ruleOffer;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("URGENT_SALE")
public class ROfferUrgentSale extends RuleOffer {
    private int tradeUpvotes;
    private double prdyVrssRt;
}
