package com.example.stock_system.realTimeStock;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/api/realtime")
public class RealTimeStockController {

    private final RealTimeStockService realTimeStockService;

    @Autowired
    public RealTimeStockController(RealTimeStockService realTimeStockService) {
        this.realTimeStockService = realTimeStockService;
    }

    @PostConstruct
    public void startWebSocketSession() throws IOException {
        realTimeStockService.start();
        System.out.println("연결이 되었습니다.");
    }

    @Operation(summary = "웹소켓 연결종료",description = "실시간 종목 시세 조회를 위한 웹소켓 연결 종료")
    @PostMapping("/stop")
    public void stopWebSocketSession() {
        realTimeStockService.stop();
    }

    @Operation(summary = "단일 종목 조회",description = "단일 종목의 실시간 시세를 조회")
    @GetMapping(value = "/{stockCode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object[]> streamByStockCode(@PathVariable String stockCode) {
        realTimeStockService.streamByStockCode(stockCode);
        return realTimeStockService.getStockDataStream(stockCode);
    }

    @Operation(summary = "단일 종목 조회 종료",description = "단일 종목의 실시간 시세를 조회 종료")
    @PostMapping("/stream/stop/{stockCode}")
    public void stopStreamingByStockCode(@PathVariable String stockCode) {
        realTimeStockService.stopStreamingByStockCode(stockCode);
    }

    @Operation(summary = "종목 조회 종료",description = "모든 조회 중인 실시간 시세 조회 종료")
    @PostMapping("/stream/stop")
    public void stopStreaming() {
        realTimeStockService.stopStreaming();
    }

    @Operation(summary = "모든 종목 조회",description = "모든 종목의 실시간 시세를 조회")
    @GetMapping(value = "/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object[]> streamAllData() {
        return realTimeStockService.getDataOnlyStream();
    }
}
