package com.example.stock_system.holdings;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.TeamAccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.holdings.dto.HoldingsDto;
import com.example.stock_system.holdings.dto.SaveClosingPrice;
import com.example.stock_system.realTimeStock.RealTimeStockService;
import com.example.stock_system.stocks.StocksService;
import com.example.stock_system.stocks.dto.StockCurrentPrice;
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

    @Scheduled(cron = "0 59 15 * * MON-FRI", zone = "Asia/Seoul")
    public void updateHoldingsWithCurrentPriceAtEndOfDay() {
        updateHoldingsWithCurrentPrice();
    }

    @Scheduled(cron = "0 10 15 * * MON-FRI", zone = "Asia/Seoul")
    public void updateHoldingsWithCurrentPriceAtMiddleOfDay() {
        updateHoldingsWithCurrentPrice();
    }


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

}
