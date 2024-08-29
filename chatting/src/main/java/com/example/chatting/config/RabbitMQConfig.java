//package com.example.chatting.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.annotation.EnableRabbit;
//import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableRabbit
//public class RabbitMQConfig {
//
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
//    private static final String CHAT_QUEUE_NAME = "chat.queue";
//    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
//    private static final String ROUTING_KEY = "chat.room.#";
//
//    @Bean
//    public Queue chatQueue() {
//        return new Queue(CHAT_QUEUE_NAME, true);
//    }
//
//    @Bean
//    public TopicExchange chatTopicExchange() {
//        return new TopicExchange(CHAT_EXCHANGE_NAME);
//    }
//
//    @Bean
//    public Binding binding(Queue chatQueue, TopicExchange exchange) {
//        return BindingBuilder.bind(chatQueue).to(exchange).with("chat.room.#");
//    }
//
//    @Bean
//    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory (org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
//        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory());
//        factory.setMessageConverter(jackson2JsonMessageConverter());
//        return factory;
//    }
//
//    @Bean
//    public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory(){
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost(rabbitmqHost);
//        connectionFactory.setPort(rabbitmqPort);
//        connectionFactory.setUsername(rabbitmqUsername);
//        connectionFactory.setPassword(rabbitmqPassword);
//        return connectionFactory;
//    }
//
////    @Bean
////    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
////        return new RabbitAdmin(connectionFactory);
////    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory){
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
//        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
//        return rabbitTemplate;
//    }
//
//    @Bean
//    public MessageConverter jackson2JsonMessageConverter() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
//        objectMapper.registerModule(new JavaTimeModule());
//        return new Jackson2JsonMessageConverter(objectMapper);
//    }
//
//
//}
