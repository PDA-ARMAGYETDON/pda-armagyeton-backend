package com.example.stock_system.realTimeStock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class RealTimeStockController {

    private final RealTimeStockService webSocketClientService;

    @Autowired
    public RealTimeStockController(RealTimeStockService webSocketClientService) {
        this.webSocketClientService = webSocketClientService;
    }

    @PostMapping("/start")
    public String startWebSocket() throws IOException {
        webSocketClientService.start();
        return "웹 소켓 연결이 시작되었습니다.";
    }


    @PostMapping("/stop")
    public String stopWebSocket(){
        webSocketClientService.stop();
        return "웹 소켓 연결 끝!";
    }



    @GetMapping("/{stockCode}")
    public String getStockData(@PathVariable String stockCode) {
        return webSocketClientService.getStockData(stockCode);
    }

    @GetMapping("/all")
    public Map<String, String> getAllStockData() {
        return webSocketClientService.getAllStockData();
    }
}



