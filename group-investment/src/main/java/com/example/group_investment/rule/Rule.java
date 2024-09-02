package com.example.group_investment.rule;

import com.example.group_investment.rule.dto.RuleDto;
import com.example.group_investment.team.Team;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Entity
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
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
    public Rule(Team team, float prdyVrssRt, int urgentTradeUpvotes, int tradeUpvotes, int depositAmt, Date period, Date payDate, float maxLossRt, float maxProfitRt) {
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

    public Rule() {

    }

    public RuleDto fromEntity(Rule rule) {
        return RuleDto.builder()
                .team(rule.getTeam())
                .prdyVrssRt(rule.getPrdyVrssRt())
                .urgentTradeUpvotes(rule.getUrgentTradeUpvotes())
                .tradeUpvotes(rule.getTradeUpvotes())
                .depositAmt(rule.getDepositAmt())
                .period(rule.getPeriod())
                .payDate(rule.getPayDate())
                .maxLossRt(rule.getMaxLossRt())
                .maxProfitRt(rule.getMaxProfitRt())
                .build();
    }
}
