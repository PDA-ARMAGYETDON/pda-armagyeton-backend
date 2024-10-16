//package com.example.stock_system.rabbitMq;
//
//import com.example.common.exception.ErrorCode;
//import com.example.stock_system.stocks.exception.StocksException;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class MqSender<D> {
//
//    @Value("${spring.rabbitmq.sendQueue.name}")
//    private String sendQueueName;
//
//    private final RabbitTemplate rabbitTemplate;
//
//    public MqSender(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//    }
//
//    public void send(D data) {
//        try {
//            //json 으로 직렬화 하여 전송
//            ObjectMapper objectMapper = new ObjectMapper();
//            String objToJson = objectMapper.writeValueAsString(data);
//
//            rabbitTemplate.convertAndSend(sendQueueName, objToJson);
//
//            log.info("[{}] 알림 전송 완료", sendQueueName);
//
//        } catch (JsonProcessingException e) {
//            throw new StocksException(ErrorCode.JACKSON_PROCESS_ERROR);
//        }
//    }
//}