package com.example.realtime_stock.askPrice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/api/ask-bid")
public class AskPriceController {

    private final AskPriceService askPriceService;

    @Autowired
    public AskPriceController(AskPriceService askPriceService) {
        this.askPriceService = askPriceService;
    }

    @PostMapping("/start")
    public String startWebSocket() throws IOException {
        askPriceService.start();
        return "웹 소켓 연결 시작";
    }

    @PostMapping("/stop")
    public String stopWebSocket() {
        askPriceService.stop();
        return "웹 소켓 연결 종료";
    }

    @GetMapping(value = "/stream/{stockCode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object[][]> streamByStockCode(@PathVariable String stockCode) {
        askPriceService.streamByStockCode(stockCode);
        return askPriceService.stream();
    }
    @PostMapping("/stream/stop")
    public String stopStreaming() {
        askPriceService.stopStreaming();
        return "데이터 스트리밍 중지";
    }
}
