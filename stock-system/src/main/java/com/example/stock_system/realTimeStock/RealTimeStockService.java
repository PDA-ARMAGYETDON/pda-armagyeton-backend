package com.example.stock_system.realTimeStock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
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
    private Sinks.Many<Object[]> sink;

    @Setter
    private String stockCode = null;

    private boolean isStreamingActive = true;

    @Autowired
    public RealTimeStockService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        initializeSink();
    }

    private void initializeSink() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    // 웹소켓 연결 시작
    public void start() throws IOException {
        if (!connections.isEmpty()) {
            System.out.println("웹 소켓이 이미 연결되어 있습니다.");
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
            }
        }
        connections.clear();
        System.out.println("모든 웹소켓 연결 종료");
    }


    private List<String> getStockCodes() throws IOException {
        ClassPathResource resource = new ClassPathResource(stockFilePath);
        Path path = resource.getFile().toPath();

        String jsonContent = Files.readString(path);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonContent, new TypeReference<List<String>>() {});
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
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(this::processStockData)
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
        if (!isStreamingActive) {
            return;
        }

            if (data.trim().startsWith("{")) {
                System.out.println("수신 된 JSON형식 데이터 " + data);
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
            System.err.println("데이터 형식이 올바르지 않습니다: " + data);
            return;
        }

        Object[] stockArray = new Object[2];
        stockArray[0] = fields[0];  // stockCode
        stockArray[1] = fields[2];  // price

        sink.tryEmitNext(stockArray);
    }

    public Flux<Object[]> stream() {
        return sink.asFlux()
                .filter(data -> stockCode == null || stockCode.equals(data[0]))
                .delayElements(Duration.ofMillis(100));
    }


    public void stopStreaming() {
        isStreamingActive = false;
    }


    public void streamByStockCode(String stockCode) {
        this.stockCode = stockCode;
        isStreamingActive = true;
    }

    public void streamAll() {
        this.stockCode = null;
        isStreamingActive = true;
    }

    private void handleError(Throwable error) {
        System.err.println("웹 소켓 에러: " + error.getMessage());
    }
}
