//package com.example.alarm.rabbitMq;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class MqReceiver {
//
//    @RabbitListener(queues = "${spring.rabbitmq.recvStockQueue.name}")
//    public void receiveStock(String message) {
//        try {
//            //TODO: Object 타임 DTO 타입으로 추후 변경 및 출력 제거
//            Object dtoObject = new ObjectMapper().readValue(message, Object.class);
//            System.out.println(" [Stock] 받은 정보: '" + dtoObject.toString() + "'");
//
//            /*
//             * 처리할 로직 작성
//             */
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            log.error("Json -> Object convert Error!!");
//        }
//    }
//
//    @RabbitListener(queues = "${spring.rabbitmq.recvChatQueue.name}")
//    public void receiveChat(String message) {
//        try {
//            //TODO: Object 타임 DTO 타입으로 추후 변경 및 출력 제거
//            Object dtoObject = new ObjectMapper().readValue(message, Object.class);
//            System.out.println(" [Chat] 받은 정보: '" + dtoObject.toString() + "'");
//
//            /*
//             * 처리할 로직 작성
//             */
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            log.error("Json -> Object convert Error!!");
//        }
//    }
//
//}
