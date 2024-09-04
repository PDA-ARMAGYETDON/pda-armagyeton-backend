package com.example.group_investment.team;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private String inviteCode;


    @Builder
    public Invitation(String inviteCode, Team team) {
        this.inviteCode = inviteCode;
        this.team = team;
    }

    public Invitation() {

    }
}
