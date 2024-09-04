package com.example.group_investment.ruleOffer;

import com.example.group_investment.enums.RulePeriod;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("PAY_FEE")
public class ROfferPayFee extends RuleOffer {
    private int depositAmt;
    private RulePeriod period;
    private LocalDateTime payDate;
}
