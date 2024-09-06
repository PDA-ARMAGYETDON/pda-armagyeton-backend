package com.example.group_investment.tradeOffer.dto;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.TradeType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TradeOfferResponse {
    private int tradeOfferId;
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
    private boolean isVote;

    public TradeOfferResponse() {
    }

    @Builder
    public TradeOfferResponse(int tradeOfferId, String userName, TradeType tradeType, String stockName, int wantPrice, int quantity, String offerAt, boolean isUrgent, OfferStatus offerStatus, int upvotes, int downvotes, boolean isVote) {
        this.tradeOfferId = tradeOfferId;
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
        this.isVote = isVote;
    }
}
