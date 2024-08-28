package com.example.realtime_stock.askPrice;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AskPriceService {

    @Value("${HANTU_APP_KEY_ASKPRICE}")
    private String appKeyRaw;

    @Value("${HANTU_APP_SECRET_KEY_ASKPRICE}")
    private String appSecretKeyRaw;

    @Value("${HANTU_SOCKET_CONNECT_URL_M}")
    private String socketConnectUrl;

    @Value("${HANTU_REALTIMESTOCK_URL_M}")
    private String askPriceUrl;

    @Value("${STOCK.FILE.PATH:stockCode.txt}")
    private String stockFilePath;

    private final List<Disposable> connections = new CopyOnWriteArrayList<>();
    private final WebClient webClient;
    private final Sinks.Many<Object[][]> sink;

    @Setter
    private String stockCode = null; // 필터링할 주식 코드

    @Autowired
    public AskPriceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.sink = Sinks.many().multicast().onBackpressureBuffer(); // 실시간 데이터를 전송하기 위한 Sink
    }

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
        return objectMapper.readValue(jsonContent, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
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
        Disposable connection = client.execute(URI.create(askPriceUrl), session ->
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
                .doOnNext(this::processData)
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
                "      \"tr_id\": \"H0STASP0\",\n" +
                "      \"tr_key\": \"" + stockCode + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private void processData(String data) {
        if (data.trim().startsWith("{")) {
            System.out.println("수신 된 JSON형식 데이터 " + data);
            return;
        }

        String[] parts = data.split("\\|");

        if (parts.length >= 4) {
            String stockData = parts[3];
            processAskPriceData(stockData);
        } else {
            System.err.println("원하지 않는 데이터 형식입니다. 해당 데이터: " + data);
        }
    }

    private void processAskPriceData(String data) {
        String[] fields = data.split("\\^");

        if (fields.length < 25) {
            System.err.println("데이터 형식이 올바르지 않습니다: " + data);
            return;
        }

        Object[][] askBidData = new Object[11][2];

        askBidData[0][0] = "stockCode";
        askBidData[0][1] = fields[0];

        for (int i = 0; i < 5; i++) {
            askBidData[i + 1][0] = fields[3 + i];  // askPrice
            askBidData[i + 1][1] = fields[23 + i]; // askQty
            askBidData[6 + i][0] = fields[13 + i]; // bidPrice
            askBidData[6 + i][1] = fields[33 + i]; // bidQty
        }

        // 필터링 로직을 적용하여 주식 코드가 일치할 때만 전송
        if (stockCode == null || stockCode.equals(askBidData[0][1])) {
            sink.tryEmitNext(askBidData);
        }
    }

    public Flux<Object[][]> stream() {
        return sink.asFlux();
    }

    private void handleError(Throwable error) {
        System.err.println("웹 소켓 에러: " + error.getMessage());
    }
}
