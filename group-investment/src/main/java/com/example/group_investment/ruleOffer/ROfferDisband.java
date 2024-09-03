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
@DiscriminatorValue("DISBAND")
public class ROfferDisband extends RuleOffer {
    private double maxLossRt;
    private double maxProfitRt;
}
