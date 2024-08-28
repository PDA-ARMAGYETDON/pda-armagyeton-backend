package com.example.stock_system.account;

import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.pre_trade.PreTrade;
import com.example.stock_system.trade.Trade;
import com.example.stock_system.transferHistory.TransferHistory;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String accountNumber;
    private int deposit;
    private int totalEvluAmt;
    private int totalPchsAmt;
    private int totalEvluPfls;
    private double totalEvluPflsRt;
    private LocalDateTime createdAt;
    private int receivingAccountId;

    @OneToMany(mappedBy = "account")
    private List<Holdings> holdings;

    @OneToMany(mappedBy = "account")
    private List<Trade> trades;

    @OneToMany(mappedBy = "account")
    private List<PreTrade> preTrades;

    @OneToMany(mappedBy = "account")
    private List<TransferHistory> transferHistories;

    @Builder
    public Account(String accountNumber, int deposit, int totalEvluAmt, int totalPchsAmt, int totalEvluPfls, double totalEvluPflsRt, int receivingAccountId) {
        this.accountNumber = accountNumber;
        this.deposit = deposit;
        this.totalEvluAmt = totalEvluAmt;
        this.totalPchsAmt = totalPchsAmt;
        this.totalEvluPfls = totalEvluPfls;
        this.totalEvluPflsRt = totalEvluPflsRt;
        this.receivingAccountId = receivingAccountId;
    }
}
