package com.example.armagyetdon.ruleOffer;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DISBAND")
public class ROfferDisband extends RuleOffer {
    private double maxLossRt;
    private double maxProfitRt;
}
