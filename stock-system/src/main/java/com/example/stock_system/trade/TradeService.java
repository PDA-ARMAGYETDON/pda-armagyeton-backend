package com.example.stock_system.trade;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.holdings.HoldingsRepository;
import com.example.stock_system.stocks.Stocks;
import com.example.stock_system.stocks.StocksRepository;
import com.example.stock_system.stocks.exception.StocksErrorCode;
import com.example.stock_system.stocks.exception.StocksException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final StocksRepository stocksRepository;
    private final HoldingsRepository holdingsRepository;
    private final AccountRepository accountRepository;

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

                System.out.println("거래 완료 - 주식 코드: " + stockCode + ", 거래 ID: " + trade.getId());
            }
            else {
                System.out.println("거래 실패 - 예치금 부족, 거래 ID: " + trade.getId());
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
            holdingsRepository.save(holdings);

            account.sellStock(profitLoss);
            accountRepository.save(account);

            System.out.println("거래 완료 - 주식 코드: " + stockCode + ", 거래 ID: " + trade.getId());
        }


    }
}
