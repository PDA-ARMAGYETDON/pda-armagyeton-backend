package com.example.stock_system.stocks;

import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.pre_trade.PreTrade;
import com.example.stock_system.trade.Trade;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Stocks {
    @Id
    private String code;

    private String name;

    @OneToMany(mappedBy = "stocks")
    private List<Trade> trades;

    @OneToMany(mappedBy = "stocks")
    private List<PreTrade> preTrades;

    @OneToMany(mappedBy = "stocks")
    private List<Holdings> holdings;
}
