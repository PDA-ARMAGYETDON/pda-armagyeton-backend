package com.example.stock_system.holdings;

import com.example.stock_system.account.Account;
import com.example.stock_system.stocks.Stocks;
import jakarta.persistence.*;

@Entity
public class Holdings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private int hldgQty;
    private int pchsAmt;
    private int evluAmt;
    private int evluPfls;
    private double evluPflsRt;

    @ManyToOne(targetEntity = Stocks.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code")
    private Stocks stocks;
}
