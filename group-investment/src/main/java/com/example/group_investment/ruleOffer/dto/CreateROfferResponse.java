package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

@Builder
public class CreateROfferResponse {
    @Enumerated(EnumType.STRING)
    RuleType type;
}
