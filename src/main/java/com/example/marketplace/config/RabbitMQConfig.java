package com.example.marketplace.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_PRICE_DROP = "price.drop.email.queue";
    public static final String EXCHANGE_PRICE_DROP = "price.drop.exchange";
    public static final String ROUTING_KEY_PRICE_DROP = "price.drop.routing.key";

    public static final String QUEUE_PRODUCT_EVENT = "product.event.queue";
    public static final String ROUTING_KEY_PRODUCT_EVENT = "product.event.routing.key";

    public static final String QUEUE_PRODUCT_EVENT_DELAY = "product.event.delay.queue";
    public static final String EXCHANGE_PRODUCT_EVENT_DELAY = "product.event.delay.exchange";
    public static final String ROUTING_KEY_PRODUCT_EVENT_DELAY = "product.event.delay.routing.key";

    /////// ORDER EXCHANGE //////

    public static final String QUEUE_CANCEL_ORDER = "cancel.order.queue";
    public static final String EXCHANGE_ORDER = "order.exchange";
    public static final String ROUTING_KEY_CANCEL_ORDER = "cancel.order.routing.key";

    @Bean
    public MessageConverter jsonMassConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue priceDropQueue() {
        return new Queue(QUEUE_PRICE_DROP, true);
    }

    @Bean
    public DirectExchange priceDropExchange() {
        return new DirectExchange(EXCHANGE_PRICE_DROP);
    }

    @Bean
    public Queue productEventQueue() {
        return new Queue(QUEUE_PRODUCT_EVENT, true);
    }

    @Bean
    public Binding priceDropBinding() {
        return BindingBuilder.bind(priceDropQueue()).to(priceDropExchange()).with(ROUTING_KEY_PRICE_DROP);
    }

    @Bean
    public Binding productEventBinding() {
        return BindingBuilder.bind(productEventQueue()).to(priceDropExchange()).with(ROUTING_KEY_PRODUCT_EVENT);
    }

    @Bean
    public DirectExchange productEventDelayExchange() {
        return new DirectExchange(EXCHANGE_PRODUCT_EVENT_DELAY);
    }

    @Bean
    public Queue productEventDelayQueue() {
        return QueueBuilder.durable(QUEUE_PRODUCT_EVENT_DELAY)
                .withArgument("x-dead-letter-exchange", EXCHANGE_PRICE_DROP)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_PRODUCT_EVENT)
                .withArgument("x-message-ttl", 150000)
                .build();
    }

    @Bean
    public Binding productEventDelayBinding() {
        return BindingBuilder.bind(productEventDelayQueue()).to(productEventDelayExchange())
                .with(ROUTING_KEY_PRODUCT_EVENT_DELAY);
    }

    /////// ORDER EXCHANGE //////

    @Bean
    public Queue cancelOrder() {
        return new Queue(QUEUE_CANCEL_ORDER, true);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_ORDER);
    }

    @Bean
    public Binding cancelOrderBinding() {
        return BindingBuilder.bind(cancelOrder()).to(orderExchange()).with(ROUTING_KEY_CANCEL_ORDER);
    }
}
