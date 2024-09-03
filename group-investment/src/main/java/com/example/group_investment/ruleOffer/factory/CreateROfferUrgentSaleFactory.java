package com.example.group_investment.ruleOffer.factory;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.ruleOffer.dto.CreateROfferRequestUrgentSale;

public class CreateROfferUrgentSaleFactory extends CreateROfferCreator<CreateROfferRequestUrgentSale> {
    public boolean supports(RuleType type) {
        return type == RuleType.URGENT_SALE;
    }

    public CreateROfferRequestUrgentSale createDto(){
        return new CreateROfferRequestUrgentSale();
    }
}
