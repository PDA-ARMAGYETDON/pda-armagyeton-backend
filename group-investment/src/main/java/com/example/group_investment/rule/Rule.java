package com.example.group_investment.rule;

import com.example.group_investment.enums.Period;
import com.example.group_investment.team.Team;
import jakarta.persistence.*;

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
    private Period period;
    private Date payDate;
    private float maxLossRt;
    private float maxProfitRt;
}
