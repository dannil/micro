package com.github.dannil.micro.personservice.configuration.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dannil.micro.personservice.configuration.PersonRoutingKey;

import org.springframework.amqp.core.*;
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
  public Queue personAddedQueue() {
    return QueueBuilder.nonDurable()
      .autoDelete()
      .exclusive()
      .deadLetterExchange(deadLetterExchange().getName())
      .build();
  }

  @Bean
  public Queue personDeletedQueue() {
    return QueueBuilder.nonDurable()
      .autoDelete()
      .exclusive()
      .deadLetterExchange(deadLetterExchange().getName())
      .build();
  }

  @Bean
  public Queue deadLetterQueue() {
    return QueueBuilder.durable(applicationName + ".deadletter").build();
  }

  @Bean
  public Binding personAddedTopicBinding() {
    return BindingBuilder.bind(personAddedQueue()).to(topicExchange()).with(PersonRoutingKey.ADDED + ".#");
  }

  @Bean
  public Binding personDeletedTopicBinding() {
    return BindingBuilder.bind(personDeletedQueue()).to(topicExchange()).with(PersonRoutingKey.DELETED + ".#");
  }

  @Bean
  public Binding deadLetterBinding() {
    return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
  }

}
