package com.example.group_investment.ranking;

import com.example.group_investment.enums.SeedMoneyCategory;
import com.example.group_investment.team.Team;
import jakarta.persistence.*;

@Entity
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private SeedMoneyCategory seedMoney;

    private double evluPflsRt;
}
