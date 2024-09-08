package com.example.stock_system.trade;

import com.example.stock_system.account.Account;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.stocks.Stocks;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private TradeType type;

    @Enumerated(EnumType.STRING)
    @Setter
    private TradeStatus status;
    private int price;
    private int quantity;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(targetEntity = Stocks.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code")
    private Stocks stockCode;

    public Trade() {
    }

    @Builder
    public Trade(Account account, TradeType type, int price, int quantity, Stocks stockCode) {
        this.account = account;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.stockCode = stockCode;
    }
}
