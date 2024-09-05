//package com.example.group_investment.rabbitMq;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class MqSender<D> {
//
//    @Value("${spring.rabbitmq.Queue.name}")
//    private String QueueName;
//
//    private final RabbitTemplate rabbitTemplate;
//
//    public void send(D data) {
//        try {
//            //json 으로 직렬화 하여 전송
//            ObjectMapper objectMapper = new ObjectMapper();
//            String objToJson = objectMapper.writeValueAsString(data);
//
//            rabbitTemplate.convertAndSend(QueueName, objToJson);
//
//            //TODO: 테스트 후 삭제 할 것
//            log.info("보낸 큐 이름: {}", QueueName);
//            log.info("[Main] Send: '{}' ", data.toString());
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            log.error("Object -> Json convert Error");
//        }
//    }
//}
