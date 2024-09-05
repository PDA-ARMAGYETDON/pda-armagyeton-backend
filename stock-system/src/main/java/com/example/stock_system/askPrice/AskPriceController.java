package com.example.stock_system.askPrice;

import io.swagger.v3.oas.annotations.Operation;
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


    @Operation(summary = "호가 처리 웹소켓 실행",description = "실시간 호가 data를 80개 받아올 수 있도록 연결합니다.")
    @PostMapping("/start")
    public void startWebSocket() throws IOException {
        askPriceService.start();
    }

    @Operation(summary = "호가 처리 웹소켓 종료",description = "실시간 호가 data를 받아오는 소켓을 종료합니다.")
    @PostMapping("/stop")
    public void stopWebSocket() {
        askPriceService.stop();
    }

    @Operation(summary = "특정 종목 호가 받아오기",description = "pathVariable로 받은 종목 코드로 해당 종목의 호가를 매수호가5개, 매도호가 5개와 각각의 잔량 형식으로 가져옵니다.")
    @GetMapping(value = "/stream/{stockCode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object[][]> streamByStockCode(@PathVariable String stockCode) {
        askPriceService.streamByStockCode(stockCode);
        return askPriceService.stream();
    }

    @Operation(summary = "특정 종목 호가 종료", description = "특정 종목 호가를 받아오는 로직을 종료합니다.")
    @PostMapping("/stream/stop")
    public void stopStreaming() {
        askPriceService.stopStreaming();
    }
}
