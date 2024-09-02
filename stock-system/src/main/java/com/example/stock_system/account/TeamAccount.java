package com.example.stock_system.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_account")
@Getter
@NoArgsConstructor
public class TeamAccount {

    @Id
    @Column(name = "account_id")
    private int accountId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;
    
    @Column(name = "team_id")
    private int teamId;

}
