package com.example.group_investment.tradeOffer.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetAllTradeOffersResponse {
    private List<TradeOfferResponse> tradeOffers;


    public GetAllTradeOffersResponse() {
    }

    @Builder
    public GetAllTradeOffersResponse(List<TradeOfferResponse> tradeOfferResponses) {
        this.tradeOffers = tradeOfferResponses;
    }
}
