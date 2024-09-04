package com.example.group_investment.tradeOffer.dto;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.TradeType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TradeOfferResponse {
    private String userName;
    private TradeType tradeType;
    private String stockName;
    private int wantPrice;
    private int quantity;
    private String offerAt;
    private boolean isUrgent;
    private OfferStatus offerStatus;
    private int upvotes;
    private int downvotes;

    public TradeOfferResponse() {
    }

    @Builder
    public TradeOfferResponse(String userName, TradeType tradeType, String stockName, int wantPrice, int quantity, String offerAt, boolean isUrgent, OfferStatus offerStatus, int upvotes, int downvotes) {
        this.userName = userName;
        this.tradeType = tradeType;
        this.stockName = stockName;
        this.wantPrice = wantPrice;
        this.quantity = quantity;
        this.offerAt = offerAt;
        this.isUrgent = isUrgent;
        this.offerStatus = offerStatus;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }
}
