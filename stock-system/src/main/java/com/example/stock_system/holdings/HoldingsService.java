package com.example.stock_system.holdings;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.TeamAccount;
import com.example.stock_system.account.TeamAccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.holdings.dto.HoldingsDto;
import com.example.stock_system.holdings.dto.SaveClosingPrice;
import com.example.stock_system.holdings.exception.HoldingsErrorCode;
import com.example.stock_system.holdings.exception.HoldingsException;
import com.example.stock_system.stocks.Stocks;
import com.example.stock_system.stocks.StocksRepository;
import com.example.stock_system.stocks.StocksService;
import com.example.stock_system.stocks.dto.StockCurrentPrice;
import com.example.stock_system.stocks.exception.StocksErrorCode;
import com.example.stock_system.stocks.exception.StocksException;
import com.example.stock_system.trade.Trade;
import com.example.stock_system.trade.TradeRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HoldingsService {

    private final HoldingsRepository holdingsRepository;
    private final AccountRepository accountRepository;
    private final StocksService stocksService;
    private final TeamAccountRepository teamAccountRepository;
    private final StocksRepository stocksRepository;
    private final TradeRepository tradeRepository;

    public List<HoldingsDto> getHoldings(int accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        List<Holdings> holdingsList = holdingsRepository.findByAccount(account);
        return holdingsList.stream().map(HoldingsDto::new).collect(Collectors.toList());
    }

    public Flux<HoldingsDto> getRealTimeHoldings(int accountId, Flux<Object[]> stockDataFlux) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        List<Holdings> holdingsList = holdingsRepository.findByAccount(account);

        return stockDataFlux.filter(stockData -> holdingsList.stream().anyMatch(holding -> holding.getStockCode().getCode().equals(stockData[0].toString()))).map(stockData -> {
            String stockCode = stockData[0].toString();
            int currentPrice = Integer.parseInt(stockData[1].toString());

            Holdings holding = holdingsList.stream().filter(h -> h.getStockCode().getCode().equals(stockCode)).findFirst().orElseThrow(() -> new HoldingsException(HoldingsErrorCode.HOLDINGS_NOT_FOUND));

            int evluAmt = currentPrice * holding.getHldgQty();
            int evluPfls = evluAmt - holding.getPchsAmt();
            double evluPflsRt = (double) evluPfls / holding.getPchsAmt() * 100;

            return new HoldingsDto(holding, evluAmt, evluPfls, evluPflsRt);
        });
    }

    @Scheduled(cron = "0 15 15 * * MON-FRI", zone = "Asia/Seoul")
    public void updateHoldingsWithCurrentPrice() {
        List<Holdings> holdingsList = holdingsRepository.findAll();

        for (Holdings holding : holdingsList) {
            String stockCode = holding.getStockCode().getCode();


            StockCurrentPrice stockCurrentPrice = stocksService.getCurrentData(stockCode);

            int currentPrice = stockCurrentPrice.getCurrentPrice();

            int evluAmt = currentPrice * holding.getHldgQty();
            int evluPfls = evluAmt - holding.getPchsAmt();
            double evluPflsRt = (double) evluPfls / holding.getPchsAmt() * 100;

            SaveClosingPrice saveClosingPrice = new SaveClosingPrice(evluAmt, evluPfls, evluPflsRt);

            holding.updateWithClosingPrice(saveClosingPrice);

            holdingsRepository.save(holding);
        }

    }

    public Integer getNumOfHoldings(int teamId, String code) {
        TeamAccount teamAccount = teamAccountRepository.findByTeamId(teamId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        Account account = teamAccount.getAccount();
        Stocks stocks = stocksRepository.findByCode(code)
                .orElseThrow(() -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));
        Holdings holdings = holdingsRepository.findByAccountAndStockCode(account, stocks)
                .orElseThrow(() -> new HoldingsException(HoldingsErrorCode.HOLDINGS_NOT_FOUND));

        return holdings.getHldgQty();
    }

    public Integer getAvailableAsset(int teamId) {
        TeamAccount teamAccount = teamAccountRepository.findByTeamId(teamId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        Account account = teamAccount.getAccount();

        List<Trade> trades = tradeRepository.findAllByAccountAndTypeAndStatus(account, TradeType.BUY, TradeStatus.PENDING)
                .orElseGet(() -> List.of());

        int pendingBuyAmount = trades.stream().mapToInt(trade -> trade.getPrice() * trade.getQuantity()).sum();

        return account.getDeposit() - pendingBuyAmount;
    }
}
