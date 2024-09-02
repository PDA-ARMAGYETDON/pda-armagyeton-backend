package com.example.group_investment.enums;

import lombok.Getter;

@Getter
public enum OfferStatus {
    PROGRESS("진행"),
    APPROVED("가결"),
    REJECTED("부결");

    private final String message;

    OfferStatus(String message) {
        this.message = message;
    }
}
