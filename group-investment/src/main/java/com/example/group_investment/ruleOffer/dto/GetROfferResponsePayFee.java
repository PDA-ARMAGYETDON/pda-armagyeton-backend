package com.example.group_investment.ruleOffer.dto;

import com.example.group_investment.enums.OfferStatus;
import com.example.group_investment.enums.RulePeriod;
import com.example.group_investment.enums.RuleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Builder
public class GetROfferResponsePayFee implements GetROfferResponseType {

    @Enumerated(EnumType.STRING)
    RuleType type;
    int id;
    OfferStatus status;
    int upvotes;
    int downvotes;
    int totalvotes;

    int depositAmt;
    RulePeriod period;
    LocalDate payDate;

    boolean isVote;

    public GetROfferResponsePayFee(RuleType type, int id, OfferStatus status, int upvotes, int downvotes, int totalvotes, int depositAmt, RulePeriod period, LocalDate payDate, boolean isVote) {
        this.type = type;
        this.id = id;
        this.status = status;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.totalvotes = totalvotes;
        this.depositAmt = depositAmt;
        this.period = period;
        this.payDate = payDate;
        this.isVote = isVote;
    }

}
