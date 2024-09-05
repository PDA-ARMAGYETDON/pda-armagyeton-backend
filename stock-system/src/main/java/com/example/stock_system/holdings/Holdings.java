package com.example.stock_system.holdings;

import com.example.stock_system.account.Account;
import com.example.stock_system.holdings.dto.SaveClosingPrice;
import com.example.stock_system.stocks.Stocks;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Holdings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    private int hldgQty;
    private int pchsAmt;
    private int evluAmt;
    private int evluPfls;
    private double evluPflsRt;

    @ManyToOne(targetEntity = Stocks.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code")
    private Stocks stockCode;


    public Holdings(Account account, Stocks stockCode,int hldgQty,int pchsAmt) {
        this.account = account;
        this.hldgQty = hldgQty;     //보유수량
        this.pchsAmt = pchsAmt;     //매입 금액
        this.stockCode = stockCode;
    }

    public Holdings() {
    }

    public void addData(int quantity,int amount) {
        this.hldgQty += quantity;
        this.pchsAmt += amount;
    }

    public void subtractData(int quantity,int amount) {
        this.hldgQty -= quantity;
        this.pchsAmt -= amount;
    }

    public void updateWithClosingPrice(SaveClosingPrice saveClosingPrice) {
        this.evluAmt = saveClosingPrice.getEvluAmt();
        this.evluPfls = saveClosingPrice.getEvluPfls();
        this.evluPflsRt = saveClosingPrice.getEvluPflsRt();
    }

}
