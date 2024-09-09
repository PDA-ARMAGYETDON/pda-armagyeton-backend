package com.example.invest_references;

import com.example.invest_references.config.IgnoreSSL;
import com.example.invest_references.dto.*;
import com.example.invest_references.exception.InvestRefErrorCode;
import com.example.invest_references.exception.InvestRefException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvestRefService {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private static final String NAVER_FINANCE_NEWS_URL = "https://finance.naver.com/news/mainnews.naver";
    private static final String NAVER_FINANCE_MARKET_INDEX_URL = "https://finance.naver.com/sise/sise_index.naver?code=";

    private final IgnoreSSL ignoreSSL;

    private final ObjectMapper objectMapper;

    private static final String SHINHAN_RISING_RANKING_URL = "https://gapi.shinhansec.com:8443/openapi/v1.0/ranking/rising";

    private static final String SHINHAN_VOLUME_RANKING_URL = "https://gapi.shinhansec.com:8443/openapi/v1.0/ranking/issue?query_type=1";

    private static final String SHINHAN_EARNING_RANKING_URL = "https://gapi.shinhansec.com:8443/openapi/v1.0/ranking/issue?query_type=2";

    @Value("${SHINHAN_APP_KEY}")
    private String shinhanAppKey;


    // 네이버 증권 주요 뉴스를 가져오는 메서드
    public List<News> getNews() {
        List<News> newsList = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(NAVER_FINANCE_NEWS_URL)
                    .userAgent(USER_AGENT)
                    .get();

            Elements newsElements = doc.select("ul.newsList > li.block1");

            // 상위 5개의 뉴스만 파싱
            for (int i = 0; i < Math.min(5, newsElements.size()); i++) {
                Element newsElement = newsElements.get(i);

                // 각 뉴스의 정보 추출
                String press = newsElement.select(".press").text().trim();  // 언론사
                String title = newsElement.select("a").text().trim();      // 뉴스 제목
                String url = "https://finance.naver.com" + newsElement.select("a").attr("href");  // 뉴스 URL
                String date = newsElement.select(".wdate").text().trim();   // 날짜

                News news = new News(press, title, url, date);
                newsList.add(news);
            }
        } catch (IOException e) {
            throw new InvestRefException(InvestRefErrorCode.FAILED_TO_GET_NEWS);
        }

        return newsList;
    }

    public MarketIndexResponse getMarketIndex() {
        try {
            Document kospiDoc = Jsoup.connect(NAVER_FINANCE_MARKET_INDEX_URL + "KOSPI")
                    .userAgent(USER_AGENT)
                    .get();

            System.out.println("kospiDoc = " + kospiDoc.title());

            String kospiNowValue = kospiDoc.select("em#now_value").text().trim();
            String kospiChangeValueAndRate = kospiDoc.select("span#change_value_and_rate").text().trim();
            String kospiChangeValue = kospiChangeValueAndRate.split(" ")[0];
            String kospiChangeRate = kospiChangeValueAndRate.split(" ")[1].replaceAll("[^0-9-.%]", "");

            Document kosdaqDoc = Jsoup.connect(NAVER_FINANCE_MARKET_INDEX_URL + "KOSDAQ")
                    .userAgent(USER_AGENT)
                    .get();

            String kosdaqNowValue = kosdaqDoc.select("em#now_value").text().trim();
            String kosdaqChangeValueAndRate = kosdaqDoc.select("span#change_value_and_rate").text().trim();
            String kosdaqChangeValue = kosdaqChangeValueAndRate.split(" ")[0];
            String kosdaqChangeRate = kosdaqChangeValueAndRate.split(" ")[1].replaceAll("[^0-9-.%]", "");

            return MarketIndexResponse.builder()
                    .kospi(Kospi.builder()
                            .currentValue(kospiNowValue)
                            .changeValue(kospiChangeValue)
                            .changeRate(kospiChangeRate)
                            .build())
                    .kosdaq(Kosdaq.builder()
                            .currentValue(kosdaqNowValue)
                            .changeValue(kosdaqChangeValue)
                            .changeRate(kosdaqChangeRate)
                            .build())
                    .build();

        } catch (IOException e) {
            throw new InvestRefException(InvestRefErrorCode.FAILED_TO_GET_MARKET_INDEX);
        }
    }


    public List<ShinhanData> getRisingRanking() throws JsonProcessingException {
        // SSL 검증을 비활성화
        ignoreSSL.disableSslVerification();

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("apiKey", shinhanAppKey);  // API 키를 헤더에 추가

            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

            ResponseEntity<String> response = restTemplate.exchange(
                    SHINHAN_RISING_RANKING_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);

            List<ShinhanData> rankings = new ArrayList<>();
            if (result != null && result.get("dataBody") != null) {
                Map<String, Object> dataBody = (Map<String, Object>) result.get("dataBody");
                List<Map<String, Object>> list = (List<Map<String, Object>>) dataBody.get("list");

                int rank = 1;
                for (Map<String, Object> item : list) {
                    String stockName = (String) item.get("stbd_nm");
                    String stockCode = (String) item.get("stock_code");

                    rankings.add(new ShinhanData(stockName, String.valueOf(rank++), stockCode));
                }
            }

            return rankings;
        }catch (IOException e){
            throw new InvestRefException(InvestRefErrorCode.FAILED_TO_GET_RANKING);
        }

    }



    public List<ShinhanData> getVolumeRanking() throws JsonProcessingException{
        ignoreSSL.disableSslVerification();

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("apiKey", shinhanAppKey);  // API 키를 헤더에 추가

            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

            ResponseEntity<String> response = restTemplate.exchange(
                    SHINHAN_VOLUME_RANKING_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);

            List<ShinhanData> rankings = new ArrayList<>();
            if (result != null && result.get("dataBody") != null) {
                List<Map<String, Object>> dataBody = (List<Map<String, Object>>) result.get("dataBody");

                for (Map<String, Object> item : dataBody) {
                    String rank = String.valueOf(item.get("rank"));
                    String stockName = (String) item.get("stbd_nm");
                    String stockCode = (String) item.get("stock_code");

                    rankings.add(new ShinhanData(stockName, rank, stockCode));
                }
            }

            return rankings;
        }catch (IOException e){
            throw new InvestRefException(InvestRefErrorCode.FAILED_TO_GET_VOLUME);
        }
    }



    public List<ShinhanData> getEarningRanking() throws JsonProcessingException{
        ignoreSSL.disableSslVerification();

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("apiKey", shinhanAppKey);  // API 키를 헤더에 추가

            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

            ResponseEntity<String> response = restTemplate.exchange(
                    SHINHAN_EARNING_RANKING_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);

            List<ShinhanData> rankings = new ArrayList<>();
            if (result != null && result.get("dataBody") != null) {
                List<Map<String, Object>> dataBody = (List<Map<String, Object>>) result.get("dataBody");

                for (Map<String, Object> item : dataBody) {
                    String rank = String.valueOf(item.get("rank"));
                    String stockName = (String) item.get("stbd_nm");
                    String stockCode = (String) item.get("stock_code");

                    rankings.add(new ShinhanData(stockName, rank, stockCode));
                }
            }

            return rankings;
        }catch (IOException e){
            throw new InvestRefException(InvestRefErrorCode.FAILED_TO_GET_EARNING);
        }
    }

    public GetIssueResponse getIssue() throws JsonProcessingException {

        List<ShinhanData> rising = getRisingRanking();
        List<ShinhanData> volume = getVolumeRanking();
        List<ShinhanData> earning = getEarningRanking();

        return GetIssueResponse.builder()
                .risingRanking(rising)
                .hotVolume(volume)
                .hotEarning(earning)
                .build();
    }
}






