package com.example.armagyetdon.ranking;

import com.example.armagyetdon.enums.SeedMoneyCategory;
import com.example.armagyetdon.team.Team;
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
