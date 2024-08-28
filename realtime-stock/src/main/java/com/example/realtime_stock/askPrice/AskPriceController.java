package com.example.realtime_stock.askPrice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/api/ask-bid")
public class AskPriceController {

    private final AskPriceService askSellPriceSocketClient;

    @Autowired
    public AskPriceController(AskPriceService asckSellPriceSocketClient) {
        this.askSellPriceSocketClient = asckSellPriceSocketClient;
    }

    @PostMapping("/start")
    public String startWebSocket() throws IOException {
        askSellPriceSocketClient.start();
        return "웹 소켓 연결 시작";
    }

    @PostMapping("/stop")
    public String stopWebSocket() {
        askSellPriceSocketClient.stop();
        return "웹 소켓 연결 종료";
    }

    @GetMapping(value = "/stream/{stockCode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object[][]> streamFilteredByStockCode(@PathVariable String stockCode) {
        askSellPriceSocketClient.setStockCode(stockCode);
        return askSellPriceSocketClient.stream();
    }
}
