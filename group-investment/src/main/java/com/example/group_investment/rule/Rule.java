package com.example.group_investment.rule;
import com.example.group_investment.enums.RulePeriod;
import com.example.group_investment.rule.dto.RuleDto;
import com.example.group_investment.team.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private double prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;

    @Enumerated(EnumType.STRING)
    private RulePeriod period;

    private LocalDate payDate;
    private double maxLossRt;
    private double maxProfitRt;


    @Builder
    public Rule(Team team, double prdyVrssRt, int urgentTradeUpvotes, int tradeUpvotes, int depositAmt, RulePeriod period, LocalDate payDate, double maxLossRt, double maxProfitRt) {
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
