package com.example.armagyetdon.rule;

import com.example.armagyetdon.team.Team;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.Date;

@Entity
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
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
}
