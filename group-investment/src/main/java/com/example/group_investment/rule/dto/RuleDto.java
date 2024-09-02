package com.example.group_investment.rule.dto;

import com.example.group_investment.rule.Rule;
import com.example.group_investment.team.Team;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class RuleDto {
    private Team team;
    private float prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;
    private Date period;
    private Date payDate;
    private float maxLossRt;
    private float maxProfitRt;

    @Builder
    public RuleDto(Team team, float prdyVrssRt, int urgentTradeUpvotes, int tradeUpvotes, int depositAmt, Date period, Date payDate, float maxLossRt, float maxProfitRt) {
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
