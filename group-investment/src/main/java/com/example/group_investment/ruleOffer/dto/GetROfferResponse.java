package com.example.group_investment.ruleOffer.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetROfferResponse {
    List<GetROfferResponsePayFee> payFeeOffers;
    List<GetROfferResponseUrgentSale> urgentSaleOffers;
    List<GetROfferResponseUpvoteNumber> upvoteNumberOffers;
    List<GetROfferResponseDisband> disbandOffers;
}
