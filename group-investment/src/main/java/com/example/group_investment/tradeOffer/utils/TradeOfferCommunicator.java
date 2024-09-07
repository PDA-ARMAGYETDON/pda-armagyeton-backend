package com.example.group_investment.tradeOffer.utils;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.tradeOffer.dto.StockName;
import com.example.group_investment.tradeOffer.exception.TradeOfferErrorCode;
import com.example.group_investment.tradeOffer.exception.TradeOfferException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class TradeOfferCommunicator {
    private final WebClient.Builder webClientBuilder;

    @Value("${ag.url}")
    private String AG_URL;

    public StockName getStockNameFromStockSystem(String code) {
        WebClient webClient = webClientBuilder.build();

        ApiResponse<StockName> stockName = webClient.get()
                .uri(AG_URL + ":8082/api/backend/stocks/names?code=" + code)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<StockName>>() {
                })
                .block();

        if (!stockName.isSuccess()) {
            throw new TradeOfferException(TradeOfferErrorCode.STOCKS_SERVER_BAD_REQUEST);
        }

        return stockName.getData();
    }

    public double getPrdyVrssRtFromStockSystem(String code) {
        WebClient webClient = webClientBuilder.build();

        ApiResponse<Double> prdyVrssRt = webClient.get()
                .uri(AG_URL + ":8082/api/backend/stocks/prdyVrssRt?code=" + code)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Double>>() {
                })
                .block();

        if (!prdyVrssRt.isSuccess()) {
            throw new TradeOfferException(TradeOfferErrorCode.STOCKS_SERVER_BAD_REQUEST);
        }

        return prdyVrssRt.getData();
    }

    public int getNumOfHoldingsFromStockSystem(int teamId, String code) {
        WebClient webClient = webClientBuilder.build();

        ApiResponse<Integer> numOfHoldings = webClient.get()
                .uri(AG_URL + ":8082/api/backend/holdings/count?teamId=" + teamId + "&code=" + code)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Integer>>() {
                })
                .block();

        if (!numOfHoldings.isSuccess()) {
            throw new TradeOfferException(TradeOfferErrorCode.STOCKS_SERVER_BAD_REQUEST);
        }

        return numOfHoldings.getData();
    }

    public int getNumOfPendingTradeFromStockSystem(int teamId, String code) {
        WebClient webClient = webClientBuilder.build();

        ApiResponse<Integer> numOfPendingTrade = webClient.get()
                .uri(AG_URL + ":8082/api/backend/trades/count?teamId=" + teamId + "&code=" + code)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Integer>>() {
                })
                .block();

        if (!numOfPendingTrade.isSuccess()) {
            throw new TradeOfferException(TradeOfferErrorCode.STOCKS_SERVER_BAD_REQUEST);
        }

        return numOfPendingTrade.getData();
    }

    public int getAvailableAssetFromStockSystem(int teamId) {
        WebClient webClient = webClientBuilder.build();

        ApiResponse<Integer> restAsset = webClient.get()
                .uri(AG_URL + ":8082/api/backend/accounts/asset?teamId=" + teamId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Integer>>() {
                })
                .block();

        if (!restAsset.isSuccess()) {
            throw new TradeOfferException(TradeOfferErrorCode.STOCKS_SERVER_BAD_REQUEST);
        }

        return restAsset.getData();
    }
}
