package com.example.group_investment.team;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Invitation {

    @Id
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
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
