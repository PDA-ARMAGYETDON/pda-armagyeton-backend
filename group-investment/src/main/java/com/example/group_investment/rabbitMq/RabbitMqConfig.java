package com.example.group_investment.rabbitMq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // 기본 세팅
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${spring.rabbitmq.fcm-queue.name}")
    private String mainToAlarm;

    @Value("${spring.rabbitmq.main-stock-queue.name}")
    private String offerToTradeQueue;

    @Value("${spring.rabbitmq.vote-alarm-queue.name}")
    private String voteToAlarmQueueName;

    @Value("${spring.rabbitmq.rule-alarm-queue.name}")
    private String ruleToAlarmQueueName;


    @Bean
    public Queue mainToAlarmQueue() {
        return new Queue(mainToAlarm, true);
    }

    @Bean
    public Queue mainToStockQueue() {
        return new Queue(offerToTradeQueue, true);
    }

    @Bean
    public Queue voteToAlarmQueue() {
        return new Queue(voteToAlarmQueueName, true);
    }

    @Bean
    public Queue ruleToAlarmQueue() {
        return new Queue(ruleToAlarmQueueName, true);
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}
