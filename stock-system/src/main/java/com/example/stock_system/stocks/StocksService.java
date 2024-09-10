package com.example.stock_system.stocks;

import com.example.stock_system.holdings.dto.ClosingPirceRequest;
import com.example.stock_system.stocks.dto.*;
import com.example.stock_system.stocks.exception.StocksErrorCode;
import com.example.stock_system.stocks.exception.StocksException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StocksService {

    private final StocksRepository stocksRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${HANTU_APP_KEY_SAVE_CLOSINGPRICE}")
    private String appKey;

    @Value("${HANTU_APP_SECRET_KEY_SAVE_CLOSINGPRICE}")
    private String appSecret;

    @Value("${HANTU_CONNECT_URL_M}")
    private String baseUrl;

    private String accessToken = null;

    private static final String STOCK_URL = "https://fchart.stock.naver.com/sise.nhn?symbol=%s&timeframe=day&count=%d&requestType=0";
    private static final Pattern pattern = Pattern.compile("[-+]?(\\d*\\.\\d+|\\d+)");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

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

        TokenResponse response;
        try {
            response = restTemplate.postForObject(tokenUri, requestBody, TokenResponse.class);
        } catch (Exception e) {
            throw new StocksException(StocksErrorCode.ACCESS_TOKEN_BAD_REQUEST);
        }

        if (response != null && response.getAccessToken() != null) {
            return response.getAccessToken();
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
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", stockCode)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.accessToken);
        headers.add("appkey", appKey);
        headers.add("appsecret", appSecret);
        headers.add("tr_id", "FHKST01010100");
        headers.add("Content-Type", "application/json; charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ClosingPirceRequest> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    ClosingPirceRequest.class
            );
        } catch (Exception e) {
            throw new StocksException(StocksErrorCode.API_BAD_RESPONSE);
        }

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

    public GetStockPricesResponse getStockPrices(String code) {
        try {
            List<StockPriceData> stockPriceData = new ArrayList<>();
            String url = String.format(STOCK_URL, code, 365);

            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select("item");

            for (Element row : items) {
                List<Integer> dailyHistory = extractStockData(String.valueOf(row));

                StockPriceData stock = StockPriceData.builder()
                        .date(LocalDate.parse(String.valueOf(dailyHistory.get(0).intValue()), DateTimeFormatter.ofPattern("yyyyMMdd")).format(formatter))
                        .price(dailyHistory.get(1))
                        .build();

                stockPriceData.add(stock);
            }

            return GetStockPricesResponse.builder()
                    .stockPrices(stockPriceData)
                    .build();
        } catch (IOException e) {
            throw new StocksException(StocksErrorCode.STOCKS_PRICE_CONNECTION_ERROR);
        }
    }

    private List<Integer> extractStockData(String rawData) {
        List<Integer> result = new ArrayList<>();

        String dataValue = rawData.replaceAll(".*data=\"([^\"]+)\".*", "$1");
        String[] dataParts = dataValue.split("\\|");

        for (String part : dataParts) {
            result.add(Integer.parseInt(part));
        }
        return result;
    }

}
