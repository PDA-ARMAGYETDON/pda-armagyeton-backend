package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.RuleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateROfferResponse {
    @Enumerated(EnumType.STRING)
    RuleType type;
}
