package com.example.group_investment.team.dto;

import com.example.group_investment.enums.RulePeriod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class DetailTeamResponse {
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int baseAmt;
    private double prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;
    private RulePeriod period;
    private LocalDate payDate;
    private double maxLossRt;
    private double maxProfitRt;

    @Builder
    public DetailTeamResponse(LocalDateTime startAt, LocalDateTime endAt, int baseAmt, double prdyVrssRt, int urgentTradeUpvotes, int tradeUpvotes, int depositAmt, RulePeriod period, LocalDate payDate, double maxLossRt, double maxProfitRt) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.baseAmt = baseAmt;
        this.prdyVrssRt = prdyVrssRt;
        this.urgentTradeUpvotes = urgentTradeUpvotes;
        this.tradeUpvotes = tradeUpvotes;
        this.depositAmt = depositAmt;
        this.period = period;
        this.payDate = payDate;
        this.maxLossRt = maxLossRt;
        this.maxProfitRt = maxProfitRt;
    }

    public DetailTeamResponse() {

    }
}
