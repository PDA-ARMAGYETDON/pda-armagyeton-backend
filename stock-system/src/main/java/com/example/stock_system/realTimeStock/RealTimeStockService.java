package com.example.stock_system.realTimeStock;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.holdings.HoldingsRepository;
import com.example.stock_system.stocks.Stocks;
import com.example.stock_system.stocks.StocksRepository;
import com.example.stock_system.trade.Trade;
import com.example.stock_system.trade.TradeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RealTimeStockService {

    @Value("${HANTU_APP_KEY_REALTIME}")
    private String appKeyRaw;

    @Value("${HANTU_APP_SECRET_KEY_REALTIME}")
    private String appSecretKeyRaw;

    @Value("${HANTU_SOCKET_CONNECT_URL_M}")
    private String socketConnectUrl;

    @Value("${HANTU_REALTIMESTOCK_URL_M}")
    private String realTimeStockUrl;

    @Value("${STOCK.FILE.PATH:stockCode.txt}")
    private String stockFilePath;

    private final List<Disposable> connections = new CopyOnWriteArrayList<>();
    private final WebClient webClient;
    private final TradeRepository tradeRepository;
    private final HoldingsRepository holdingsRepository;
    private final AccountRepository accountRepository;
    private final StocksRepository stocksRepository;

    private Sinks.Many<Object[]> allStockDataSink;
    private Sinks.Many<Object[]> dataOnlySink;
    private final Map<String, Sinks.Many<Object[]>> individualStockSinks = new ConcurrentHashMap<>();
    private Sinks.Many<Integer> totalSumSink;
    private final Map<String, Boolean> isStreamingActiveMap = new ConcurrentHashMap<>();

    @Autowired
    public RealTimeStockService(WebClient.Builder webClientBuilder, TradeRepository tradeRepository,
                                HoldingsRepository holdingsRepository, AccountRepository accountRepository,
                                StocksRepository stocksRepository) {
        this.webClient = webClientBuilder.build();
        this.tradeRepository = tradeRepository;
        this.holdingsRepository = holdingsRepository;
        this.accountRepository = accountRepository;
        this.stocksRepository = stocksRepository;
    }


    public void start() throws IOException {
        if (allStockDataSink != null) {
            System.out.println("WebSocket이 이미 연결되어 있습니다.");
            return;
        }

        allStockDataSink = Sinks.many().multicast().onBackpressureBuffer();
        dataOnlySink = Sinks.many().multicast().onBackpressureBuffer();

        List<String> appKeys = List.of(appKeyRaw.split(","));
        List<String> appSecretKeys = List.of(appSecretKeyRaw.split(","));

        if (appKeys.size() != appSecretKeys.size()) {
            throw new IllegalArgumentException("App Key와 Secret Key의 개수가 일치하지 않습니다.");
        }

        List<String> stockCodes = getStockCodes();

        int maxProcessableCodes = Math.min(stockCodes.size(), appKeys.size() * 40);
        int stockCodeIndex = 0;

        for (int i = 0; i < appKeys.size() && stockCodeIndex < maxProcessableCodes; i++) {
            String appKey = appKeys.get(i).trim();
            String appSecretKey = appSecretKeys.get(i).trim();
            String approvalKey = getApprovalKey(appKey, appSecretKey);

            int endIdx = Math.min(stockCodeIndex + 40, maxProcessableCodes);
            List<String> batch = stockCodes.subList(stockCodeIndex, endIdx);
            connectToWebSocket(batch, approvalKey);

            stockCodeIndex += 40;
        }

        // 매매 로직
        allStockDataSink.asFlux().subscribe(stockData -> {
            String stockCode = (String) stockData[0];
            int price = (Integer) stockData[1];
            processPendingTrades(stockCode, price);
        });

        // 데이터만 처리하는 로직
        allStockDataSink.asFlux().subscribe(stockData -> {
            if (dataOnlySink != null) {
                dataOnlySink.tryEmitNext(stockData);
            }
        });
    }

    public void stop() {
        for (Disposable connection : connections) {
            if (connection != null && !connection.isDisposed()) {
                connection.dispose();
                System.out.println("연결 해제됨: " + connection);
            }
        }
        connections.clear();
        allStockDataSink = null;
        dataOnlySink = null;
        individualStockSinks.clear();
        totalSumSink = null;
        isStreamingActiveMap.clear();
        System.out.println("모든 WebSocket 연결 종료 및 스트림 정리.");
    }

    private List<String> getStockCodes() throws IOException {
        ClassPathResource resource = new ClassPathResource(stockFilePath);
        Path path = resource.getFile().toPath();

        String jsonContent = Files.readString(path);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonContent, new TypeReference<List<String>>() {
        });
    }

    private String getApprovalKey(String appKey, String appSecretKey) {
        return webClient.post()
                .uri(socketConnectUrl)
                .bodyValue(Map.of(
                        "grant_type", "client_credentials",
                        "appkey", appKey,
                        "secretkey", appSecretKey
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("approval_key"))
                .block();
    }

    private void connectToWebSocket(List<String> stockCodes, String approvalKey) {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        Disposable connection = client.execute(URI.create(realTimeStockUrl), session ->
                handleWebSocketSession(session, stockCodes, approvalKey)
        ).subscribe();
        connections.add(connection);
    }

    private Mono<Void> handleWebSocketSession(WebSocketSession session, List<String> stockCodes, String approvalKey) {
        return Mono.when(
                stockCodes.stream()
                        .map(stockCode -> session.send(Mono.just(session.textMessage(createSubscribeMessage(stockCode, approvalKey)))))
                        .toArray(Mono[]::new)
        ).thenMany(session.receive()
                .doOnNext(message -> processStockData(message.getPayloadAsText()))
                .doOnError(this::handleError)
        ).then();
    }


    private String createSubscribeMessage(String stockCode, String approvalKey) {
        return "{\n" +
                "  \"header\": {\n" +
                "    \"approval_key\": \"" + approvalKey + "\",\n" +
                "    \"custtype\": \"P\",\n" +
                "    \"tr_type\": \"1\",\n" +
                "    \"content-type\": \"utf-8\"\n" +
                "  },\n" +
                "  \"body\": {\n" +
                "    \"input\": {\n" +
                "      \"tr_id\": \"H0STCNT0\",\n" +
                "      \"tr_key\": \"" + stockCode + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private void processStockData(String data) {
        if (data.trim().startsWith("{")) {
            return;
        }

        String[] parts = data.split("\\|");

        if (parts.length >= 4) {
            String stockData = parts[3];
            parseStockData(stockData);
        }
    }

    private void parseStockData(String data) {
        String[] fields = data.split("\\^");

        if (fields.length < 3) {
            System.err.println("올바르지 않은 데이터 형식: " + data);
            return;
        }

        String stockCode = fields[0];
        int price;

        price = Integer.parseInt(fields[2]);

        Object[] stockArray = new Object[2];
        stockArray[0] = stockCode;
        stockArray[1] = price;


        if (allStockDataSink != null) {
            allStockDataSink.tryEmitNext(stockArray);
        }


        if (dataOnlySink != null) {
            dataOnlySink.tryEmitNext(stockArray);
        }


        Sinks.Many<Object[]> stockSink = individualStockSinks.get(stockCode);
        if (stockSink != null && Boolean.TRUE.equals(isStreamingActiveMap.get(stockCode))) {
            stockSink.tryEmitNext(stockArray);
        }
    }

    protected void processPendingTrades(String stockCode, int price) {
        Stocks findStock = stocksRepository.findByCode(stockCode);

        List<Trade> pendingTrades = tradeRepository.findByStatusAndStockCodeAndPrice(TradeStatus.PENDING, findStock, price);

        for (Trade trade : pendingTrades) {
            Account account = trade.getAccount();
            int requiredAmount = trade.getPrice() * trade.getQuantity();

            if (account.getDeposit() >= requiredAmount) {
                System.out.println("거래 완료 - 주식 코드: " + stockCode + ", 거래 ID: " + trade.getId());

                trade.setStatus(TradeStatus.COMPLETED);
                tradeRepository.save(trade);

                Holdings existingHolding = holdingsRepository.findByAccountAndStockCode(account, findStock);

                if (existingHolding != null) {
                    existingHolding.addData(trade.getQuantity(), requiredAmount);
                    holdingsRepository.save(existingHolding);
                } else {
                    Holdings newHolding = new Holdings(account, findStock, trade.getQuantity(), requiredAmount);
                    holdingsRepository.save(newHolding);
                }

                account.buyStock(requiredAmount);
                accountRepository.save(account);
            }
            else {
                System.out.println("거래 실패 - 예치금 부족, 거래 ID: " + trade.getId());
            }
        }
    }


    public void streamByStockCode(String stockCode) {
        isStreamingActiveMap.put(stockCode, true);
        System.out.println("해당 코드 시세 조회 - 코드: " + stockCode);

        Sinks.Many<Object[]> stockSink = individualStockSinks.computeIfAbsent(stockCode, k -> Sinks.many().multicast().onBackpressureBuffer());

        allStockDataSink.asFlux()
                .filter(data -> stockCode.equals(data[0]))
                .map(data -> new Object[]{data[0], data[1]})
                .subscribe(stockSink::tryEmitNext);
    }

    public Flux<Object[]> getStockDataStream(String stockCode) {
        Sinks.Many<Object[]> stockSink = individualStockSinks.computeIfAbsent(stockCode, k -> Sinks.many().multicast().onBackpressureBuffer());
        return stockSink.asFlux()
                .filter(data -> Boolean.TRUE.equals(isStreamingActiveMap.get(stockCode)))
                .delayElements(Duration.ofMillis(100));
    }

    public Flux<Object[]> getDataOnlyStream() {
        if (dataOnlySink == null) {
            throw new IllegalStateException("데이터 스트림이 초기화x 웹소켓 연결 필요");
        }
        return dataOnlySink.asFlux().delayElements(Duration.ofMillis(100));
    }

    public void stopStreamingByStockCode(String stockCode) {
        isStreamingActiveMap.put(stockCode, false);
        System.out.println("스트리밍 중지됨 - 주식 코드: " + stockCode);
    }

    public void streamByStockCodes(List<String> stockCodes) {
        System.out.println("특정 주식 코드 리스트로 스트리밍 시작 - 코드들: " + stockCodes);

        if (totalSumSink != null) {
            totalSumSink.tryEmitComplete();
        }
        totalSumSink = Sinks.many().multicast().onBackpressureBuffer();

        Map<String, Integer> stockPrices = new ConcurrentHashMap<>();

        stockCodes.forEach(stockCode -> {
            isStreamingActiveMap.put(stockCode, true);

            allStockDataSink.asFlux()
                    .filter(data -> stockCode.equals(data[0]))
                    .map(data -> (Integer) data[1])
                    .subscribe(price -> {
                        stockPrices.put(stockCode, price);
                        calculateTotalSum(stockPrices);
                    });
        });
    }

    private void calculateTotalSum(Map<String, Integer> stockPrices) {
        int totalSum = stockPrices.values().stream().mapToInt(Integer::intValue).sum();
        if (totalSumSink != null) {
            totalSumSink.tryEmitNext(totalSum);
        }
    }



    public Flux<Integer> getTotalSumStream() {
        return totalSumSink.asFlux().delayElements(Duration.ofMillis(100));
    }

    public void stopStreaming() {
        isStreamingActiveMap.clear();
        System.out.println("모든 스트리밍 중지됨.");
    }

    private void handleError(Throwable error) {
        System.err.println("웹 소켓 에러 발생: " + error.getMessage());
    }
}
