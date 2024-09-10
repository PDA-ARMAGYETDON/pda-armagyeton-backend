package com.example.stock_system.realTimeStock;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.dto.GetTeamAccountResponse;
import com.example.stock_system.holdings.dto.HoldingsDto;
import com.example.stock_system.holdings.exception.HoldingsErrorCode;
import com.example.stock_system.holdings.exception.HoldingsException;
import com.example.stock_system.stocks.StocksService;
import com.example.stock_system.stocks.dto.StockName;
import com.example.stock_system.trade.TradeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
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

    @Value("${STOCK_FILE}")
    private String stockFilePath;

    private final List<Disposable> connections = new CopyOnWriteArrayList<>();
    private final WebClient webClient;

    private final TradeService tradeService;
    private final StocksService stocksService;

    private Sinks.Many<Object[]> allStockDataSink;
    private Sinks.Many<Object[]> dataOnlySink;
    private final Map<String, Sinks.Many<Object[]>> individualStockSinks = new ConcurrentHashMap<>();
    private final Map<String, Boolean> isStreamingActiveMap = new ConcurrentHashMap<>();
    private final Map<Integer, Disposable> activeStreams = new ConcurrentHashMap<>();

    private final Map<String, HoldingsDto> holdingsMap = new ConcurrentHashMap<>();

    @Autowired
    public RealTimeStockService(WebClient.Builder webClientBuilder, StocksService stocksService, TradeService tradeService) {
        this.webClient = webClientBuilder.build();
        this.stocksService = stocksService;
        this.tradeService = tradeService;
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
            String stockCode = stockData[1].toString();
            int price = Integer.parseInt(stockData[2].toString());
            tradeService.buyProcessPendingTrades(stockCode, price);
            tradeService.sellProcessPendingTrades(stockCode, price);
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
        isStreamingActiveMap.clear();
        System.out.println("모든 WebSocket 연결 종료 및 스트림 정리.");
    }

    private List<String> getStockCodes() throws IOException {
        // getResourceAsStream을 사용하여 클래스패스에서 파일을 읽어옴
        try (InputStream inputStream = getClass().getResourceAsStream(stockFilePath)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + stockFilePath);
            }
            // InputStream을 문자열로 변환
            String jsonContent = new String(inputStream.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열을 List<String>으로 변환
            return objectMapper.readValue(jsonContent, new TypeReference<List<String>>() {
            });
        }
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
                .map(response -> response.get("approval_key").toString())
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
        int price = Integer.parseInt(fields[2]);
        int prdyVrss = Integer.parseInt(fields[4]); //전일 대비
        double prdyCtrt = Double.parseDouble(fields[5]); //전일 대비율
        StockName stockName = stocksService.getStockNameByCode(stockCode);


        Object[] stockArray = new Object[5];

        stockArray[0] = stockName.getName();
        stockArray[1] = stockCode;
        stockArray[2] = price;
        stockArray[3] = prdyVrss;
        stockArray[4] = prdyCtrt;


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


    public Flux<HoldingsDto> getRealTimeHoldings(List<HoldingsDto> holdingsDtoList, List<String> stockCodes) {
        return allStockDataSink.asFlux()
                .filter(stockData -> stockCodes.contains(stockData[1].toString()))  // 주식 코드를 필터링
                .map(stockData -> {
                    String stockCode = stockData[1].toString();
                    int currentPrice = Integer.parseInt(stockData[2].toString());

                    HoldingsDto holdingsDto = holdingsDtoList.stream()
                            .filter(dto -> dto.getStockCode().equals(stockCode))
                            .findFirst()
                            .orElseThrow(() -> new HoldingsException(HoldingsErrorCode.HOLDINGS_NOT_FOUND));

                    int evluAmt = currentPrice * holdingsDto.getHldgQty(); // 평가 금액
                    int evluPfls = evluAmt - holdingsDto.getPchsAmt(); // 평가 손익
                    double evluPflsRt = (double) evluPfls / holdingsDto.getPchsAmt() * 100; // 평가 손익률

                    return new HoldingsDto(holdingsDto.getHldgQty(), holdingsDto.getPchsAmt(), evluAmt, evluPfls, evluPflsRt, stockCode, holdingsDto.getStockName());
                });
    }

    public void stopStreaming() {
        isStreamingActiveMap.clear();
        System.out.println("모든 스트리밍 중지됨.");
    }

    private void handleError(Throwable error) {
        System.err.println("웹 소켓 에러 발생: " + error.getMessage());
    }


    public Flux<GetTeamAccountResponse> getRealTimeHoldingsWithTotalSum(Account account, List<HoldingsDto> holdingsDtoList, List<String> stockCodes, int teamId) {
        if (activeStreams.containsKey(teamId)) {
            activeStreams.get(teamId).dispose();
        }

        Flux<GetTeamAccountResponse> responseFlux = allStockDataSink.asFlux()
                .filter(stockData -> stockCodes.contains(stockData[1].toString()))
                .map(stockData -> {
                    String stockCode = stockData[1].toString();
                    int currentPrice = Integer.parseInt(stockData[2].toString());

                    HoldingsDto holdingsDto = holdingsDtoList.stream()
                            .filter(dto -> dto.getStockCode().equals(stockCode))
                            .findFirst()
                            .orElseThrow(() -> new HoldingsException(HoldingsErrorCode.HOLDINGS_NOT_FOUND));

                    int evluAmt = currentPrice * holdingsDto.getHldgQty();
                    int evluPfls = evluAmt - holdingsDto.getPchsAmt();
                    double evluPflsRt = (double) evluPfls / holdingsDto.getPchsAmt() * 100;

                    holdingsDto = new HoldingsDto(holdingsDto.getHldgQty(), holdingsDto.getPchsAmt(), evluAmt, evluPfls, evluPflsRt, stockCode, holdingsDto.getStockName());
                    holdingsMap.put(stockCode, holdingsDto);

                    int totalPchsAmt = holdingsMap.values().stream().mapToInt(HoldingsDto::getPchsAmt).sum();
                    int totalEvluAmt = holdingsMap.values().stream().mapToInt(HoldingsDto::getEvluAmt).sum();
                    int totalEvluPfls = totalEvluAmt - totalPchsAmt;
                    double totalEvluPflsRt = totalPchsAmt > 0 ? (double) totalEvluPfls / totalPchsAmt * 100 : 0.0;
                    totalEvluPflsRt = roundToTwoDecimalPlaces(totalEvluPflsRt);
                    int deposit = account.getDeposit();
                    int totalAsset = totalEvluAmt + deposit;

                    return GetTeamAccountResponse.builder()
                            .accountNumber(account.getAccountNumber())
                            .totalPchsAmt(totalPchsAmt)
                            .totalEvluAmt(totalEvluAmt)
                            .totalEvluPfls(totalEvluPfls)
                            .totalEvluPflsRt(totalEvluPflsRt)
                            .deposit(deposit)
                            .totalAsset(totalAsset)
                            .build();
                })
                .switchIfEmpty(Flux.interval(Duration.ofSeconds(1))
                        .map(tick -> {
                            int totalPchsAmt = 0;
                            int totalEvluAmt = 0;
                            int totalEvluPfls = 0;
                            double totalEvluPflsRt = 0.0;
                            int deposit = account.getDeposit();
                            int totalAsset = deposit;

                            return GetTeamAccountResponse.builder()
                                    .accountNumber(account.getAccountNumber())
                                    .totalPchsAmt(totalPchsAmt)
                                    .totalEvluAmt(totalEvluAmt)
                                    .totalEvluPfls(totalEvluPfls)
                                    .totalEvluPflsRt(totalEvluPflsRt)
                                    .deposit(deposit)
                                    .totalAsset(totalAsset)
                                    .build();
                        }))
                .doFinally(signalType -> activeStreams.remove(teamId));

        Disposable disposable = responseFlux.subscribe();
        activeStreams.put(teamId, disposable);

        return responseFlux;
    }


    public void stopStreamingByTeamId(int teamId) {
        if (activeStreams.containsKey(teamId)) {
            activeStreams.get(teamId).dispose();
            activeStreams.remove(teamId);
        }
    }

    private double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}