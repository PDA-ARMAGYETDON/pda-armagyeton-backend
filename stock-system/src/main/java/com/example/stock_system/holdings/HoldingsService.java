package com.example.stock_system.holdings;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.TeamAccount;
import com.example.stock_system.account.TeamAccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.holdings.dto.GetHoldingsRatioResponse;
import com.example.stock_system.holdings.dto.HoldingsDto;
import com.example.stock_system.holdings.dto.SaveClosingPrice;
import com.example.stock_system.holdings.exception.HoldingsErrorCode;
import com.example.stock_system.holdings.exception.HoldingsException;
import com.example.stock_system.realTimeStock.RealTimeStockService;
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
    private final TeamAccountRepository teamAccountRepository;
    private final StocksService stocksService;
    private final RealTimeStockService realTimeStockService;
    private final StocksRepository stocksRepository;
    private final TradeRepository tradeRepository;

    public List<HoldingsDto> getHoldingsByTeamId(int teamId) {
        Account account = teamAccountRepository.findByTeamId(teamId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND))
                .getAccount();

        List<Holdings> holdings = holdingsRepository.findByAccount(account);

        return holdings.stream()
                .map(HoldingsDto::new)
                .collect(Collectors.toList());
    }

    public Flux<HoldingsDto> getRealTimeHoldingsByTeamId(int teamId) {
        List<HoldingsDto> holdingsDtoList = getHoldingsByTeamId(teamId);

        List<String> stockCodes = holdingsDtoList.stream()
                .map(HoldingsDto::getStockCode)
                .collect(Collectors.toList());


        return realTimeStockService.getRealTimeHoldings(holdingsDtoList, stockCodes);
    }

    @Scheduled(cron = "0 55 15 * * MON-FRI", zone = "Asia/Seoul")
    public void updateHoldingsWithCurrentPriceAtEndOfDay() throws InterruptedException {
        updateHoldingsWithCurrentPrice();
    }

    @Scheduled(cron = "0 10 15 * * MON-FRI", zone = "Asia/Seoul")
    public void updateHoldingsWithCurrentPriceAtMiddleOfDay() throws InterruptedException {
        updateHoldingsWithCurrentPrice();
    }


    public void updateHoldingsWithCurrentPrice() throws InterruptedException {
        List<Holdings> holdingsList = holdingsRepository.findAll();

        for (Holdings holding : holdingsList) {

            String stockCode = holding.getStockCode().getCode();

            Thread.sleep(1000);

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

    public List<GetHoldingsRatioResponse> getHoldingsRatio(int teamId) {
        TeamAccount teamAccount = teamAccountRepository.findByTeamId(teamId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        Account account = teamAccount.getAccount();

        if (account.getTotalEvluAmt() == 0) {
            throw new AccountException(AccountErrorCode.NO_HOLDINGS);
        }

        List<Holdings> holdings = holdingsRepository.findByAccount(account);
        return holdings.stream()
                .map(holding -> GetHoldingsRatioResponse.builder()
                        .stockName(holding.getStockCode().getName())
                        .ratio(Math.round((holding.getEvluAmt() / (double) account.getTotalEvluAmt()) * 100) / 100.0)
                        .build())
                .collect(Collectors.toList());
    }
}
