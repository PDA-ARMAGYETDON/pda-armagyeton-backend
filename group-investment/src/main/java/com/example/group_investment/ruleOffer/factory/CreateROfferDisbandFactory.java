package com.example.group_investment.ruleOffer.factory;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.ruleOffer.dto.CreateROfferRequestDisband;

public class CreateROfferDisbandFactory extends CreateROfferCreator<CreateROfferRequestDisband> {

    public boolean supports(RuleType type) {
        return type == RuleType.DISBAND;
    }

    public CreateROfferRequestDisband createDto(){
        return new CreateROfferRequestDisband();
    }

}
