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
    private String inviteUrl;


    @Builder
    public Invitation(String inviteCode, String inviteUrl, Team team) {
        this.inviteCode = inviteCode;
        this.inviteUrl = inviteUrl;
        this.team = team;
    }

    public Invitation() {

    }
}
