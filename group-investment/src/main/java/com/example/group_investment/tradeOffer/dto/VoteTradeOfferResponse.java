package com.example.group_investment.tradeOffer.dto;

import com.example.group_investment.enums.OfferStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VoteTradeOfferResponse {
    private OfferStatus offerStatus;
    private int upvotes;
    private int downvotes;

    public VoteTradeOfferResponse() {
    }

    @Builder
    public VoteTradeOfferResponse(OfferStatus offerStatus, int upvotes, int downvotes) {
        this.offerStatus = offerStatus;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }
}
