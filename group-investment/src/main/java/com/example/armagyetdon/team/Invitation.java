package com.example.armagyetdon.team;

import jakarta.persistence.*;
import lombok.Builder;

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

}
