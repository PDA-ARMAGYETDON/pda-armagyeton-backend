package com.example.invest_references;

import com.example.invest_references.dto.News;
import com.example.invest_references.exception.InvestRefErrorCode;
import com.example.invest_references.exception.InvestRefException;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InvestRefService {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private static final String NAVER_FINANCE_NEWS_URL = "https://finance.naver.com/news/mainnews.naver";

    // 네이버 증권 주요 뉴스를 가져오는 메서드
    public List<News> getNews() {
        List<News> newsList = new ArrayList<>();

        try {
            // Jsoup을 사용해 네이버 증권 뉴스 페이지의 HTML 가져오기
            Document doc = Jsoup.connect(NAVER_FINANCE_NEWS_URL)
                    .userAgent(USER_AGENT)
                    .get();

            // 뉴스 리스트를 가져오는 selector (사진에서 제공된 HTML 구조 기반)
            Elements newsElements = doc.select("ul.newsList > li.block1");

            // 상위 5개의 뉴스만 파싱
            for (int i = 0; i < Math.min(5, newsElements.size()); i++) {
                Element newsElement = newsElements.get(i);

                // 각 뉴스의 정보 추출
                String press = newsElement.select(".press").text().trim();  // 언론사
                String title = newsElement.select("a").text().trim();      // 뉴스 제목
                String url = "https://finance.naver.com" + newsElement.select("a").attr("href");  // 뉴스 URL
                String date = newsElement.select(".wdate").text().trim();   // 날짜

                // 뉴스 DTO에 저장
                News newsDTO = new News(press, title, url, date);
                newsList.add(newsDTO);
            }
        } catch (IOException e) {
            throw new InvestRefException(InvestRefErrorCode.FAILED_TO_GET_NEWS);
        }

        return newsList;
    }
}
