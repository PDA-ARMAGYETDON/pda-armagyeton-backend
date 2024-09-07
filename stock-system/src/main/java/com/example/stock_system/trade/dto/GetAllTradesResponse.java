package com.example.stock_system.trade.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetAllTradesResponse {
    private List<TradeResponse> trades;

    public GetAllTradesResponse() {
    }

    @Builder
    public GetAllTradesResponse(List<TradeResponse> trades) {
        this.trades = trades;
    }
}
