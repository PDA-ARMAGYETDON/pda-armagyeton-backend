package com.example.stock_system.pre_trade;

import com.example.stock_system.account.Account;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.stocks.Stocks;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreTrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDate startAt;
    private LocalDate endAt;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    private int price;
    private int quantity;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(targetEntity = Stocks.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code")
    private Stocks stocks;

    @Builder
    public PreTrade(Account account, LocalDate startAt, LocalDate endAt, TradeStatus status, int price, int quantity, LocalDateTime createdAt, Stocks stocks) {
        this.account = account;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.stocks = stocks;
    }
}
