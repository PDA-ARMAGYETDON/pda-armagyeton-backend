package com.example.armagyetdon.enums;

import lombok.Getter;

@Getter
public enum RuleType {
    PAY_FEE("납부규칙"),
    UPVOTE_NUMBER("매매찬성인원"),
    URGENT_SALE("긴급매도"),
    DISBAND("해체");

    private final String message;

    RuleType(String message){
        this.message = message;
    }
}
