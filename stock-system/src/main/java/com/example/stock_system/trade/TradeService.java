package com.example.stock_system.trade;

import com.example.common.exception.ErrorCode;
import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.TeamAccount;
import com.example.stock_system.account.TeamAccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.holdings.HoldingsRepository;
import com.example.stock_system.holdings.exception.HoldingsErrorCode;
import com.example.stock_system.holdings.exception.HoldingsException;
import com.example.stock_system.holdings.dto.ToAlarmDto;
import com.example.stock_system.rabbitMq.MqSender;
import com.example.stock_system.stocks.Stocks;
import com.example.stock_system.stocks.StocksRepository;
import com.example.stock_system.stocks.exception.StocksErrorCode;
import com.example.stock_system.stocks.exception.StocksException;
import com.example.stock_system.trade.dto.CreateTradeRequest;
import com.example.stock_system.trade.dto.TradeDto;
import com.example.stock_system.trade.exception.TradeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TradeService {
    private final AccountRepository accountRepository;
    private final StocksRepository stocksRepository;
    private final HoldingsRepository holdingsRepository;
    private final TradeRepository tradeRepository;
    private final TeamAccountRepository teamAccountRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.sendQueue.name}")
    private String sendQueueName;

    @RabbitListener(queues = "${spring.rabbitmq.mainToStock.name}")
    public void createTrade(String message) {
        CreateTradeRequest createTradeRequest;
        try {
            createTradeRequest = new ObjectMapper().readValue(message, CreateTradeRequest.class);
        } catch (JsonProcessingException e) {
            throw new TradeException(ErrorCode.JSON_PARSE_ERROR);
        } catch (AmqpException e) {
            throw new TradeException(ErrorCode.MQ_CONNECTION_FAILED);
        }

        TeamAccount teamAccount = teamAccountRepository.findByTeamId(createTradeRequest.getTeamId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.TEAM_ACCOUNT_NOT_FOUND));

        Account account = teamAccount.getAccount();

        Stocks stocks = stocksRepository.findByCode(createTradeRequest.getStockCode())
                .orElseThrow(() -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));

        if (createTradeRequest.getTradeType() == TradeType.BUY) {
            if (createTradeRequest.getPrice() * createTradeRequest.getQuantity() > account.getDeposit()) {
                throw new AccountException(AccountErrorCode.NOT_ENOUGH_DEPOSIT);
            }
        } else {
            Holdings holdings = holdingsRepository.findByAccountAndStockCode(account, stocks)
                    .orElseThrow(() -> new HoldingsException(HoldingsErrorCode.HOLDINGS_NOT_FOUND));
            List<Trade> trades = tradeRepository.findAllByAccountAndStockCodeAndTypeAndStatus(account, stocks, TradeType.SELL, TradeStatus.PENDING)
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

                Holdings existingHolding = holdingsRepository.findByAccountAndStockCode(account, findStock)
                        .orElseThrow(() -> new HoldingsException(HoldingsErrorCode.HOLDINGS_NOT_FOUND));

                if (existingHolding != null) {
                    existingHolding.addData(trade.getQuantity(), requiredAmount);
                    holdingsRepository.save(existingHolding);
                } else {
                    Holdings newHolding = new Holdings(account, findStock, findStock.getName(), trade.getQuantity(), requiredAmount);
                    holdingsRepository.save(newHolding);
                }
                account.buyStock(requiredAmount);
                accountRepository.save(account);

                //알람 전송 파트
                int teamId = teamAccountRepository.findByAccountId(account.getUserId()).orElseThrow(
                        () -> new AccountException(AccountErrorCode.TEAM_ACCOUNT_NOT_FOUND)).getTeamId();

                MqSender<ToAlarmDto> mqSender = new MqSender<>(rabbitTemplate);
                ToAlarmDto data = new ToAlarmDto(teamId, account.getName(), findStock.getName(), trade.getQuantity(), true);

                mqSender.send(data);


            } else {
                log.info("매수 실패 - 예치금 부족, 거래 ID: {}", trade.getId());
            }
        }
    }


    public void sellProcessPendingTrades(String stockCode, int price) {
        Stocks findStock = stocksRepository.findByCode(stockCode).orElseThrow(() -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));

        List<Trade> pendingTrades = tradeRepository.findByStatusAndStockCodeAndPriceAndType(TradeStatus.PENDING, findStock, price, TradeType.SELL);

        for (Trade trade : pendingTrades) {

            trade.setStatus(TradeStatus.COMPLETED);
            tradeRepository.save(trade);

            Account account = trade.getAccount();

            int profitLoss = trade.getPrice() * trade.getQuantity();

            Holdings holdings = holdingsRepository.findByAccountAndStockCode(account, findStock)
                    .orElseThrow(() -> new HoldingsException(HoldingsErrorCode.HOLDINGS_NOT_FOUND));

            holdings.subtractData(trade.getQuantity(), profitLoss);
            if (holdings.getHldgQty() == 0) {
                holdingsRepository.delete(holdings);
            } else {
                holdingsRepository.save(holdings);
            }

            account.sellStock(profitLoss);
            accountRepository.save(account);

            //알람 전송 파트
            int teamId = teamAccountRepository.findByAccountId(account.getUserId()).orElseThrow(
                    () -> new AccountException(AccountErrorCode.TEAM_ACCOUNT_NOT_FOUND)).getTeamId();

            MqSender<ToAlarmDto> mqSender = new MqSender<>(rabbitTemplate);
            ToAlarmDto data = new ToAlarmDto(teamId, account.getName(), findStock.getName(), trade.getQuantity(), false);

            mqSender.send(data);

            System.out.println("매도 완료 - 주식 코드: " + stockCode + ", 거래 ID: " + trade.getId());
        }

    }

}
