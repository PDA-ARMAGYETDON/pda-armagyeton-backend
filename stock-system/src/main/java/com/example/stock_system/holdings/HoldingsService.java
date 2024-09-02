package com.example.stock_system.holdings;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.holdings.dto.HoldingsDto;
import com.example.stock_system.realTimeStock.RealTimeStockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HoldingsService {

    private final HoldingsRepository holdingsRepository;
    private final AccountRepository accountRepository;
    private final RealTimeStockService realTimeStockService;

    public List<HoldingsDto> getHoldings(int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        List<Holdings> holdingsList = holdingsRepository.findByAccount(account);
        return holdingsList.stream()
                .map(HoldingsDto::new)
                .collect(Collectors.toList());
    }

    public Flux<HoldingsDto> getRealTimeHoldings(int accountId, Flux<Object[]> stockDataFlux) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        List<Holdings> holdingsList = holdingsRepository.findByAccount(account);

        return stockDataFlux
                .filter(stockData -> holdingsList.stream()
                        .anyMatch(holding -> holding.getStockCode().getCode().equals(stockData[0].toString())))
                .map(stockData -> {
                    String stockCode = stockData[0].toString();
                    int currentPrice = Integer.parseInt(stockData[1].toString());

                    Holdings holding = holdingsList.stream()
                            .filter(h -> h.getStockCode().getCode().equals(stockCode))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Invalid stock code: " + stockCode));

                    int evluAmt = currentPrice * holding.getHldgQty();
                    int evluPfls = evluAmt - holding.getPchsAmt();
                    double evluPflsRt = (double) evluPfls / holding.getPchsAmt() * 100;

                    return new HoldingsDto(holding, evluAmt, evluPfls, evluPflsRt);
                });
    }


}
