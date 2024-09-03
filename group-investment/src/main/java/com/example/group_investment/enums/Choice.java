package com.example.group_investment.enums;

import lombok.Getter;

@Getter
public enum Choice {
    PROS("찬성"),
    CONS("반대");

    private final String message;

    Choice(String message) {
        this.message = message;
    }
}
