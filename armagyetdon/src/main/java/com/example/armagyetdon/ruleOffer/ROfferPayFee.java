package com.example.armagyetdon.ruleOffer;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("PAY_FEE")
public class ROfferPayFee extends RuleOffer {
    private int depositAmt;
    private LocalDateTime period;
    private LocalDateTime payDate;
}
