package com.example.realtime_stock.realTimeStock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RealTimeStockService {

    @Value("${HANTU_APP_KEY}")
    private String appKeyRaw;

    @Value("${HANTU_APP_SECRET_KEY}")
    private String appSecretKeyRaw;

    @Value("${HANTU_SOCKET_CONNECT_URL_M}")
    private String socketConnectUrl;

    @Value("${HANTU_REALTIMESTOCK_URL_M}")
    private String realTimeStockUrl;

    @Value("${STOCK.FILE.PATH:stockCode.txt}")
    private String stockFilePath;

    private final List<Disposable> connections = new CopyOnWriteArrayList<>();
    private final Map<String, String> stockDataMap = new ConcurrentHashMap<>();
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public RealTimeStockService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
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

        // 최대 처리 가능한 stockCode의 개수는 appKey의 개수 * 40
        int maxProcessableCodes = Math.min(stockCodes.size(), appKeys.size() * 40);

        int stockCodeIndex = 0;

        for (int i = 0; i < appKeys.size() && stockCodeIndex < maxProcessableCodes; i++) {
            String appKey = appKeys.get(i).trim();
            String appSecretKey = appSecretKeys.get(i).trim();
            String approvalKey = getApprovalKey(appKey, appSecretKey);

            // 각 appKey는 최대 40개의 stockCode를 처리
            int endIdx = Math.min(stockCodeIndex + 40, maxProcessableCodes);
            List<String> batch = stockCodes.subList(stockCodeIndex, endIdx);
            connectToWebSocket(batch, approvalKey);

            stockCodeIndex += 40;
        }
    }


    // 모든 웹소켓 연결 종료
    public void stop() {
        for (Disposable connection : connections) {
            if (connection != null && !connection.isDisposed()) {
                connection.dispose();
            }
        }
        connections.clear();
        System.out.println("모든 웹소켓 연결 종료");
    }

    // 주식 코드를 파일에서 JSON 배열로 가져오는 메서드
    private List<String> getStockCodes() throws IOException {
        ClassPathResource resource = new ClassPathResource(stockFilePath);
        Path path = resource.getFile().toPath();

        String jsonContent = Files.readString(path);
        return objectMapper.readValue(jsonContent, new TypeReference<List<String>>() {
        });
    }

    // 웹소켓 연결 키를 발급받는 메서드
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

    // 웹소켓 연결
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
        try {
            if (data.trim().startsWith("{")) {
                System.out.println("수신 된 JSON형식 데이터 " + data);
                return;
            }

            String[] parts = data.split("\\|");

            if (parts.length >= 4) {
                String stockData = parts[3];
                parseStockData(stockData);
            } else {
                System.err.println("원하지 않는 데이터 형식입니다. 해당 데이터: " + data);
            }
        } catch (Exception e) {
            System.err.println("데이터 가공 실패: " + e.getMessage());
        }
    }

    private void parseStockData(String data) {
        String[] fields = data.split("\\^");

        if (fields.length < 3) {
            System.err.println("데이터 형식이 올바르지 않습니다: " + data);
            return;
        }

        String stockCode = fields[0];
        String tradingTime = fields[1];
        String price = fields[2];

        stockDataMap.put(stockCode, String.format("체결 시간: %s, 체결 가격: %s", tradingTime, price));
    }

    private void handleError(Throwable error) {
        System.err.println("웹 소켓 에러: " + error.getMessage());


    }

    // 특정 종목 코드에 대한 데이터 조회
    public String getStockData(String stockCode) {
        return stockDataMap.get(stockCode);
    }

    // 모든 종목 코드에 대한 데이터 조회
    public Map<String, String> getAllStockData() {
        return stockDataMap;
    }
}
