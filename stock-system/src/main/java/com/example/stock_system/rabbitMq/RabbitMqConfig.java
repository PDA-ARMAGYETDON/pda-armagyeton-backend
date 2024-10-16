//package com.example.stock_system.rabbitMq;
//
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMqConfig {
//
//    // 기본 세팅
//    @Value("${spring.rabbitmq.host}")
//    private String rabbitmqHost;
//
//    @Value("${spring.rabbitmq.port}")
//    private int rabbitmqPort;
//
//    @Value("${spring.rabbitmq.username}")
//    private String rabbitmqUsername;
//
//    @Value("${spring.rabbitmq.password}")
//    private String rabbitmqPassword;
//
//    @Value("${spring.rabbitmq.sendQueue.name}")
//    private String sendQueueName;
//
//    @Value("${spring.rabbitmq.mainToStock.name}")
//    private String mainToStockQueueName;
//
//    @Bean
//    public Queue stockToAlarmQueue() {
//        return new Queue(sendQueueName, true);
//    }
//
//    @Bean
//    public Queue mainToStockQueue() {
//        return new Queue(mainToStockQueueName, true);
//    }
//
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost(rabbitmqHost);
//        connectionFactory.setPort(rabbitmqPort);
//        connectionFactory.setUsername(rabbitmqUsername);
//        connectionFactory.setPassword(rabbitmqPassword);
//        return connectionFactory;
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(jsonMessageConverter());
//        return template;
//    }
//
//}
