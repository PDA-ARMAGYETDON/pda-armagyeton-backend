package com.example.invest_references.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MarketIndexResponse {
    private final Kospi kospi;
    private final Kosdaq kosdaq;

    @Builder
    public MarketIndexResponse(Kospi kospi, Kosdaq kosdaq) {
        this.kospi = kospi;
        this.kosdaq = kosdaq;
    }
}
