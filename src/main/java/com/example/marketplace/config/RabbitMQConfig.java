package com.example.marketplace.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_PRICE_DROP = "price.drop.email.queue";
    public static final String EXCHANGE_PRICE_DROP = "price.drop.exchange";
    public static final String ROUTING_KEY_PRICE_DROP = "price.drop.routing.key";

    @Bean
    public Queue priceDropQueue() {
        return new Queue(QUEUE_PRICE_DROP, true);
    }

    @Bean
    public DirectExchange priceDropExchange() {
        return new DirectExchange(EXCHANGE_PRICE_DROP);
    }

    @Bean
    public Binding priceDropBinding() {
        return BindingBuilder.bind(priceDropQueue()).to(priceDropExchange()).with(ROUTING_KEY_PRICE_DROP);
    }

    @Bean
    public MessageConverter jsonMassConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
