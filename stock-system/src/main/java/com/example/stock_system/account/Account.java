package com.example.stock_system.account;

import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.pre_trade.PreTrade;
import com.example.stock_system.trade.Trade;
import com.example.stock_system.transferHistory.TransferHistory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    private String name;
    private String accountNumber;
    private int deposit;
    private int totalEvluAmt;
    private int totalPchsAmt;
    private int totalEvluPfls;
    private double totalEvluPflsRt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "account")
    private List<Holdings> holdings;

    @OneToMany(mappedBy = "account")
    private List<Trade> trades;

    @OneToMany(mappedBy = "account")
    private List<PreTrade> preTrades;

    @OneToMany(mappedBy = "account")
    private List<TransferHistory> transferHistories;

    public Account() {
    }

    public Account(String name,int userId,String accountNumber) {
        this.name = name;
        this.userId = userId;
        this.accountNumber = accountNumber;
    }

    @Builder
    public Account(String accountNumber, int userId, String name, int deposit, int totalEvluAmt, int totalPchsAmt, int totalEvluPfls, double totalEvluPflsRt) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.userId = userId;
        this.deposit = deposit;
        this.totalEvluAmt = totalEvluAmt;
        this.totalPchsAmt = totalPchsAmt;
        this.totalEvluPfls = totalEvluPfls;
        this.totalEvluPflsRt = totalEvluPflsRt;
    }

    public void buyStock(int price) {
        this.deposit -= price;
    }

    public void sellStock(int price) {
        this.deposit += price;
    }
}
