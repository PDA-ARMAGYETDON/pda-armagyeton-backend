package com.example.group_investment.enums;

import lombok.Getter;

@Getter
public enum JoinStatus {

    ACTIVE("참가 중"),
    DONE("완료"),
    DROP("도중 탈퇴");

    private final String message;

    JoinStatus(String message) {
        this.message = message;
    }
}
