package com.example.invest_references.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Kospi {
    private final String currentValue;
    private final String changeValue;
    private final String changeRate;

    @Builder
    public Kospi(String currentValue, String changeValue, String changeRate) {
        this.currentValue = currentValue;
        this.changeValue = changeValue;
        this.changeRate = changeRate;
    }
}
