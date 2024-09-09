package com.example.stock_system.ranking;


import com.example.stock_system.account.Account;
import com.example.stock_system.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private int teamId;
    private int seedMoney;
    private double evluPflsRt;

    public Ranking(Category category, Account account, int teamId, int seedMoney, double evluPflsRt) {
        this.category = category;
        this.account = account;
        this.teamId = teamId;
        this.seedMoney = seedMoney;
        this.evluPflsRt = evluPflsRt;
    }
    public Ranking() {

    }
}
