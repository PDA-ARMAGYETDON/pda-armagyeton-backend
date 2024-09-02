package com.example.group_investment.ruleOffer.factory;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.ruleOffer.dto.CreateROfferRequestPayFee;

public class CreateROfferPayFeeFactory extends CreateROfferCreator<CreateROfferRequestPayFee> {

    public boolean supports(RuleType type) {
        return type == RuleType.PAY_FEE;
    }

    public CreateROfferRequestPayFee createDto(){
        return new CreateROfferRequestPayFee();
    }
}
