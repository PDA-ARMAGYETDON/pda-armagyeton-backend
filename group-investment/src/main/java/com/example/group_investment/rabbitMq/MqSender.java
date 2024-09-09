package com.example.group_investment.rabbitMq;

import com.example.common.exception.ErrorCode;
import com.example.group_investment.ruleOffer.dto.VoteRuleToAlarmDTO;
import com.example.group_investment.ruleOffer.exception.RuleOfferException;
import com.example.group_investment.tradeOffer.dto.VoteStockToAlarmDto;
import com.example.group_investment.tradeOffer.exception.TradeOfferException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqSender<D> {

    @Value("${spring.rabbitmq.vote-alarm-queue.name}")
    private String ruleToAlarmQueueName;

    @Value("${spring.rabbitmq.vote-alarm-queue.name}")
    private String voteToAlarmQueueName;

    private final RabbitTemplate rabbitTemplate;


    public void send(VoteStockToAlarmDto data) {
        try {
            //json 으로 직렬화 하여 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String objToJson = objectMapper.writeValueAsString(data);

            rabbitTemplate.convertAndSend(voteToAlarmQueueName, objToJson);

        } catch (JsonProcessingException e) {
            throw new TradeOfferException(ErrorCode.JSON_PARSE_ERROR);
        }
    }


    public void send(VoteRuleToAlarmDTO data) {
        try {
            //json 으로 직렬화 하여 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String objToJson = objectMapper.writeValueAsString(data);

            rabbitTemplate.convertAndSend(ruleToAlarmQueueName, objToJson);

        } catch (JsonProcessingException e) {
            throw new RuleOfferException(ErrorCode.JSON_PARSE_ERROR);
        }
    }
}
