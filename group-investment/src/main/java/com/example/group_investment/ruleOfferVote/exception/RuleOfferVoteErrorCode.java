package com.example.group_investment.ruleOfferVote.exception;


import lombok.Getter;

@Getter
public enum RuleOfferVoteErrorCode {
    RULE_OFFER_VOTE_SAVE_FAILED(400, "AG001", "규칙 제안 투표 저장에 실패했습니다"),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    RuleOfferVoteErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }

}
