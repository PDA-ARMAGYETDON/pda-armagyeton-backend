package com.example.group_investment.rule.dto;

import com.example.group_investment.enums.RulePeriod;
import com.example.group_investment.rule.Rule;
import com.example.group_investment.team.Team;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
public class RuleDto {
    private Team team;
    private double prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;
    private RulePeriod period;
    private LocalDate payDate;

    private double maxLossRt;
    private double maxProfitRt;

    @Builder
    public RuleDto(Team team, double prdyVrssRt, int urgentTradeUpvotes, int tradeUpvotes, int depositAmt, RulePeriod period, LocalDate payDate, double maxLossRt, double maxProfitRt) {
        this.team = team;
        this.prdyVrssRt = prdyVrssRt;
        this.urgentTradeUpvotes = urgentTradeUpvotes;
        this.tradeUpvotes = tradeUpvotes;
        this.depositAmt = depositAmt;
        this.period = period;
        this.payDate = payDate;
        this.maxLossRt = maxLossRt;
        this.maxProfitRt = maxProfitRt;
    }

    public Rule toEntity() {
        return Rule.builder()
                .team(this.team)
                .prdyVrssRt(this.prdyVrssRt)
                .urgentTradeUpvotes(this.urgentTradeUpvotes)
                .tradeUpvotes(this.tradeUpvotes)
                .depositAmt(this.depositAmt)
                .period(this.period)
                .payDate(this.payDate)
                .maxLossRt(this.maxLossRt)
                .maxProfitRt(this.maxProfitRt)
                .build();
    }

}
