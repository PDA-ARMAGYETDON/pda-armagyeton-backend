package com.example.stock_system.realTimeStock;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.holdings.HoldingsRepository;
import com.example.stock_system.stocks.Stocks;
import com.example.stock_system.stocks.StocksRepository;
import com.example.stock_system.stocks.exception.StocksErrorCode;
import com.example.stock_system.stocks.exception.StocksException;
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

    private enum StreamMode {STOCK_DATA, TOTAL_SUM}

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

    private final Map<String, Sinks.Many<Object[]>> stockDataSinks = new ConcurrentHashMap<>();
    private final Map<String, Integer> stockPrices = new ConcurrentHashMap<>();
    private final Map<String, Sinks.Many<Integer>> userTotalSumSinks = new ConcurrentHashMap<>();
    private final Map<String, Boolean> isStreamingActiveMap = new ConcurrentHashMap<>();

    private StreamMode currentMode = StreamMode.STOCK_DATA;

    @Autowired
    public RealTimeStockService(WebClient.Builder webClientBuilder, TradeRepository tradeRepository, HoldingsRepository holdingsRepository, AccountRepository accountRepository, StocksRepository stocksRepository) {
        this.webClient = webClientBuilder.build();
        this.tradeRepository = tradeRepository;
        this.holdingsRepository = holdingsRepository;
        this.accountRepository = accountRepository;
        this.stocksRepository = stocksRepository;
    }

    public void start() throws IOException {
        if (!connections.isEmpty()) {
            System.out.println("WebSocket이 이미 연결되어 있습니다.");
            return;
        }

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
    }

    public void stop() {
        for (Disposable connection : connections) {
            if (connection != null && !connection.isDisposed()) {
                connection.dispose();
                System.out.println("연결 해제됨: " + connection);
            }
        }
        connections.clear();
        System.out.println("모든 WebSocket 연결 종료.");
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
        if (!isStreamingActiveMap.containsValue(true)) {
            return;
        }

        if (data.trim().startsWith("{")) {
            System.out.println("수신된 JSON 데이터: " + data);
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
        Object[] stockArray = new Object[2];
        stockArray[0] = stockCode;  // 주식 코드
        stockArray[1] = fields[2];  // 가격

        Sinks.Many<Object[]> stockSink = stockDataSinks.get(stockCode);
        if (stockSink != null && Boolean.TRUE.equals(isStreamingActiveMap.get(stockCode))) {
            stockSink.tryEmitNext(stockArray);
        }

        if (currentMode == StreamMode.STOCK_DATA) {
            processPendingTrades(stockCode, Integer.parseInt(fields[2]));
        } else if (currentMode == StreamMode.TOTAL_SUM) {
            stockPrices.put(stockCode, Integer.parseInt(fields[2]));
            calculateTotalSum();
        }
    }

    protected void processPendingTrades(String stockCode, int price) {
        Stocks findStock = stocksRepository.findByCode(stockCode).orElseThrow(() -> new StocksException(StocksErrorCode.STOCKS_NOT_FOUND));
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
            } else {
                System.out.println("거래 실패 - 예치금 부족, 거래 ID: " + trade.getId());
            }
        }
    }

    public void streamByStockCode(String stockCode) {
        currentMode = StreamMode.STOCK_DATA;
        isStreamingActiveMap.put(stockCode, true);
        System.out.println("특정 주식 코드로 스트리밍 시작 - 코드: " + stockCode);

        Sinks.Many<Object[]> stockSink = stockDataSinks.computeIfAbsent(stockCode, k -> Sinks.many().multicast().onBackpressureBuffer());
    }

    public Flux<Object[]> getStockDataStream(String stockCode) {
        Sinks.Many<Object[]> stockSink = stockDataSinks.computeIfAbsent(stockCode, k -> Sinks.many().multicast().onBackpressureBuffer());
        return stockSink.asFlux()
                .filter(data -> Boolean.TRUE.equals(isStreamingActiveMap.get(stockCode)))
                .delayElements(Duration.ofMillis(100));
    }

    public void stopStreamingByStockCode(String stockCode) {
        isStreamingActiveMap.put(stockCode, false);
        System.out.println("스트리밍 중지됨 - 주식 코드: " + stockCode);
    }

    public void streamByStockCodes(List<String> stockCodes) {
        currentMode = StreamMode.TOTAL_SUM;
        System.out.println("특정 주식 코드 리스트로 스트리밍 시작 - 코드들: " + stockCodes);

        stockCodes.forEach(stockCode -> {
            Sinks.Many<Object[]> stockSink = stockDataSinks.computeIfAbsent(stockCode, k -> Sinks.many().multicast().onBackpressureBuffer());
            isStreamingActiveMap.put(stockCode, true);
            stockSink.asFlux().subscribe(data -> {
                if (Boolean.TRUE.equals(isStreamingActiveMap.get(stockCode))) {
                    // 각 주식 코드에 대한 실시간 값을 갱신
                    stockPrices.put((String) data[0], Integer.parseInt((String) data[1]));
                    calculateTotalSum(); // 갱신된 값을 이용해 합계를 계산
                }
            });
        });
    }

    private void calculateTotalSum() {
        // 각 호출마다 총합을 다시 계산
        int totalSum = stockPrices.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(isStreamingActiveMap.get(entry.getKey())))
                .mapToInt(Map.Entry::getValue)
                .sum();

        // 실시간 합계 전송
        userTotalSumSinks.values().forEach(sink -> sink.tryEmitNext(totalSum));
    }


    public Flux<Integer> getTotalSumStream() {
        String userId = Thread.currentThread().getName();
        Sinks.Many<Integer> totalSumSink = userTotalSumSinks.computeIfAbsent(userId, k -> Sinks.many().multicast().onBackpressureBuffer());
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
