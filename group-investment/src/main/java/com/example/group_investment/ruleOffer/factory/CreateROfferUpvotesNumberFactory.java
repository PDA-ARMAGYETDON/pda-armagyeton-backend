package com.example.group_investment.ruleOffer.factory;

import com.example.group_investment.enums.RuleType;
import com.example.group_investment.ruleOffer.dto.CreateROfferRequestUpvoteNumber;

public class CreateROfferUpvotesNumberFactory extends CreateROfferCreator<CreateROfferRequestUpvoteNumber> {

    public boolean supports(RuleType type) {
        return type == RuleType.UPVOTE_NUMBER;
    }

    public CreateROfferRequestUpvoteNumber createDto(){
        return new CreateROfferRequestUpvoteNumber();
    }
}
