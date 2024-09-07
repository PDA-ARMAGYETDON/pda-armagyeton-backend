package com.example.group_investment.tradeOffer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPendingTradeOfferResponse {
    private String stockName;
    private int tradePrice;
    private int quantity;
    private String offerMemberName;
    private int Upvotes;
    private int Downvotes;
    private boolean isUrgent;

    @Builder
    public GetPendingTradeOfferResponse(String stockName, int tradePrice, int quantity, String offerMemberName, int Upvotes, int Downvotes, boolean isUrgent) {
        this.stockName = stockName;
        this.tradePrice = tradePrice;
        this.quantity = quantity;
        this.offerMemberName = offerMemberName;
        this.Upvotes = Upvotes;
        this.Downvotes = Downvotes;
        this.isUrgent = isUrgent;
    }
}
