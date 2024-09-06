package com.github.dannil.micro.accountservice.configuration.rabbitmq;

import com.github.dannil.micro.accountservice.configuration.AccountRoutingKey;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

  @Value("${spring.application.name}")
  private String applicationName;

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }

  @Bean
  public MessageConverter messageConverter(ObjectMapper jsonMapper) {
    return new Jackson2JsonMessageConverter(jsonMapper);
  }

  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(applicationName + ".topic");
  }

  @Bean
  public FanoutExchange deadLetterExchange() {
    return new FanoutExchange(applicationName + ".dlx");
  }

  @Bean
  public Queue accountAddedQueue() {
    return QueueBuilder.nonDurable().autoDelete().exclusive().deadLetterExchange(deadLetterExchange().getName()).build();
  }

  @Bean
  public Queue accountDeletedQueue() {
    return QueueBuilder.nonDurable().autoDelete().exclusive().deadLetterExchange(deadLetterExchange().getName()).build();
  }

  @Bean
  public Queue deadLetterQueue() {
    return QueueBuilder.durable(applicationName + ".deadletter").build();
  }

  @Bean
  public Binding accountAddedTopicBinding() {
    return BindingBuilder.bind(accountAddedQueue()).to(topicExchange()).with(AccountRoutingKey.ADDED + ".#");
  }

  @Bean
  public Binding accountDeletedTopicBinding() {
    return BindingBuilder.bind(accountDeletedQueue()).to(topicExchange()).with(AccountRoutingKey.DELETED + ".#");
  }

  @Bean
  public Binding deadLetterBinding() {
    return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
  }

}
