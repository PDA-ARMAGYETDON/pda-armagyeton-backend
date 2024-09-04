package com.example.stock_system.trade;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.holdings.HoldingsRepository;
import com.example.stock_system.holdings.exception.HoldingsErrorCode;
import com.example.stock_system.holdings.exception.HoldingsException;
import com.example.stock_system.stocks.Stocks;
import com.example.stock_system.stocks.StocksRepository;
import com.example.stock_system.stocks.exception.StocksErrorCode;
import com.example.stock_system.stocks.exception.StocksException;
import com.example.stock_system.trade.dto.CreateTradeRequest;
import com.example.stock_system.trade.dto.TradeDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class TradeService {
    private final AccountRepository accountRepository;
    private final StocksRepository stocksRepository;
    private final HoldingsRepository holdingsRepository;
    private final TradeRepository tradeRepository;

    public void createTrade(CreateTradeRequest createTradeRequest) {
        Account account = accountRepository.findById(createTradeRequest.getAccountId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        Stocks stocks = stocksRepository.findByCode(createTradeRequest.getStockCode())
                .orElseThrow(() -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));

        if (createTradeRequest.getTradeType() == TradeType.BUY) {
            if (createTradeRequest.getPrice() * createTradeRequest.getQuantity() > account.getDeposit()) {
                throw new AccountException(AccountErrorCode.NOT_ENOUGH_DEPOSIT);
            }
        } else {
            Holdings holdings = holdingsRepository.findByAccountAndStockCode(account, stocks)
                    .orElseThrow(() -> new HoldingsException(HoldingsErrorCode.HOLDINGS_NOT_FOUND));
            List<Trade> trades = tradeRepository.findAllByAccountAndStocksAndTypeAndStatus(account, stocks, TradeType.SELL, TradeStatus.PENDING)
                    .orElseGet(Collections::emptyList);

            int tradeQuantity = 0;
            for (Trade trade : trades) {
                tradeQuantity += trade.getQuantity();
            }

            if (createTradeRequest.getQuantity() > (holdings.getHldgQty() - tradeQuantity)) {
                throw new HoldingsException(HoldingsErrorCode.NOT_ENOUGH_HOLDINGS);
            }
        }

        TradeDto tradeDto = TradeDto.builder()
                .account(account)
                .type(createTradeRequest.getTradeType())
                .stocks(stocks)
                .quantity(createTradeRequest.getQuantity())
                .price(createTradeRequest.getPrice())
                .build();

        tradeRepository.save(tradeDto.toEntity());
    }


    public void buyProcessPendingTrades(String stockCode, int price) {
        Stocks findStock = stocksRepository.findByCode(stockCode).orElseThrow(() -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));

        List<Trade> pendingTrades = tradeRepository.findByStatusAndStockCodeAndPriceAndType(TradeStatus.PENDING, findStock, price, TradeType.BUY);

        for (Trade trade : pendingTrades) {
            Account account = trade.getAccount();
            int requiredAmount = trade.getPrice() * trade.getQuantity();

            if (account.getDeposit() >= requiredAmount) {

                trade.setStatus(TradeStatus.COMPLETED);
                tradeRepository.save(trade);

                Holdings existingHolding = holdingsRepository.findByAccountAndStockCode(account, findStock);

                if (existingHolding != null) {
                    existingHolding.addData(trade.getQuantity(), requiredAmount);
                    holdingsRepository.save(existingHolding);
                } else {
                    Holdings newHolding = new Holdings(account, findStock, trade.getQuantity(), requiredAmount);
                    holdingsRepository.save(newHolding);
                }
                account.buyStock(requiredAmount);
                accountRepository.save(account);

                System.out.println("매수 완료 - 주식 코드: " + stockCode + ", 거래 ID: " + trade.getId());
            }
            else {
                System.out.println("매수 실패 - 예치금 부족, 거래 ID: " + trade.getId());
            }
        }
    }


    public void sellProcessPendingTrades(String stockCode, int price){
        Stocks findStock = stocksRepository.findByCode(stockCode).orElseThrow(() -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));

        List<Trade> pendingTrades = tradeRepository.findByStatusAndStockCodeAndPriceAndType(TradeStatus.PENDING, findStock, price, TradeType.SELL);

        for(Trade trade : pendingTrades){

            trade.setStatus(TradeStatus.COMPLETED);
            tradeRepository.save(trade);

            Account account = trade.getAccount();

            int profitLoss = trade.getPrice()* trade.getQuantity();

            Holdings holdings = holdingsRepository.findByAccountAndStockCode(account,findStock);

            holdings.subtractData(trade.getQuantity(),profitLoss);
            if(holdings.getHldgQty()==0) {
                holdingsRepository.delete(holdings);
            }
            else {
                holdingsRepository.save(holdings);
            }

            account.sellStock(profitLoss);
            accountRepository.save(account);

            System.out.println("매도 완료 - 주식 코드: " + stockCode + ", 거래 ID: " + trade.getId());
        }

    }
}
