package com.example.stock_system.realTimeStock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/stocks/realtime")
public class RealTimeStockController {

    private final RealTimeStockService realTimeStockService;

    @Autowired
    public RealTimeStockController(RealTimeStockService realTimeStockService) {
        this.realTimeStockService = realTimeStockService;
    }

    @PostMapping("/start")
    public String startWebSocketSession() throws IOException {
            realTimeStockService.start();
            return "웹 소켓 연결이 시작되었습니다.";

    }

    @PostMapping("/stop")
    public String stopWebSocketSession() {
        realTimeStockService.stop();
        return "웹 소켓 연결 종료";
    }

    @GetMapping(value = "/{stockCode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object[]> streamByStockCode(@PathVariable String stockCode) {
        realTimeStockService.streamByStockCode(stockCode);
        return realTimeStockService.getStockDataStream(stockCode);
    }

    @PostMapping(value = "/sum", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> streamSumByStockCodes(@RequestBody List<String> stockCodes) {
        realTimeStockService.streamByStockCodes(stockCodes);
        return realTimeStockService.getTotalSumStream();
    }

    @PostMapping("/stream/stop/{stockCode}")
    public String stopStreamingByStockCode(@PathVariable String stockCode) {
        realTimeStockService.stopStreamingByStockCode(stockCode);
        return "데이터 스트리밍 중지 - 주식 코드: " + stockCode;
    }

    @PostMapping("/stream/stop")
    public String stopStreaming() {
        realTimeStockService.stopStreaming();
        return "모든 데이터 스트리밍 중지";
    }
}
