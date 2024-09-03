package com.example.stock_system.stocks;

import com.example.stock_system.holdings.dto.ClosingPirceRequest;
import com.example.stock_system.stocks.dto.StockCurrentPrice;
import com.example.stock_system.stocks.dto.StockName;
import com.example.stock_system.stocks.dto.TokenResponse;
import com.example.stock_system.stocks.exception.StocksErrorCode;
import com.example.stock_system.stocks.exception.StocksException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StocksService {

    private final StocksRepository stocksRepository;

    private final RestTemplate restTemplate = new RestTemplate();  // RestTemplate 초기화

    @Value("${HANTU_APP_KEY_SAVE_CLOSINGPRICE}")
    private String appKey;

    @Value("${HANTU_APP_SECRET_KEY_SAVE_CLOSINGPRICE}")
    private String appSecret;

    @Value("${HANTU_CONNECT_URL_M}")
    private String baseUrl;

    private String accessToken = null;

    public StockName getStockNameByCode(String stockCode) {
        Stocks stocks = stocksRepository.findByCode(stockCode)
                .orElseThrow(() -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));
        return new StockName().fromEntity(stocks);
    }

    public String getAccessToken() {
        String tokenUri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/oauth2/tokenP")
                .toUriString();

        Map<String, String> requestBody = Map.of(
                "grant_type", "client_credentials",
                "appkey", appKey,
                "appsecret", appSecret
        );

        TokenResponse response = restTemplate.postForObject(tokenUri, requestBody, TokenResponse.class);

        if (response != null && response.getAccessToken() != null) {
            return "엑세스 토큰이 생성되었습니다.";
        } else {
            throw new StocksException(StocksErrorCode.API_BAD_RESPONSE);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void refreshAccessToken() {
        this.accessToken = getAccessToken();
    }

    public StockCurrentPrice getCurrentData(String stockCode) {
        if (this.accessToken == null) {
            this.accessToken = getAccessToken();
        }

        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/uapi/domestic-stock/v1/quotations/inquire-price")
                .queryParam("fid_cond_mrkt_div_code", "J")
                .queryParam("fid_input_iscd", stockCode)
                .toUriString();

        // HttpHeaders 객체를 생성하고 헤더를 설정합니다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.accessToken);
        headers.add("appkey", appKey);
        headers.add("appsecret", appSecret);
        headers.add("tr_id", "FHKST01010100");
        headers.add("Content-Type", "application/json; charset=utf-8");


        HttpEntity<String> entity = new HttpEntity<>(headers);


        ResponseEntity<ClosingPirceRequest> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                ClosingPirceRequest.class
        );

        ClosingPirceRequest response = responseEntity.getBody();

        if (response == null || response.getOutput() == null) {
            throw new StocksException(StocksErrorCode.API_BAD_RESPONSE);
        }

        String stckPrpr = response.getOutput().get("stck_prpr");
        String prdyCtrt = response.getOutput().get("prdy_ctrt");

        int currentPrice = Integer.parseInt(stckPrpr);
        double changeRate = Double.parseDouble(prdyCtrt);

        return new StockCurrentPrice(currentPrice, changeRate);
    }
}
