package com.github.dannil.micro.accountservice.service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.github.dannil.micro.accountservice.configuration.AccountEvent;
import com.github.dannil.micro.accountservice.configuration.AccountRoutingKey;
import com.github.dannil.micro.accountservice.eventbus.AccountMulticastBackpressureEventBus;
import com.github.dannil.micro.accountservice.model.AccountDto;
import com.github.dannil.micro.accountservice.model.AccountEntity;
import com.github.dannil.micro.accountservice.repository.AccountPostgresRepository;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Sinks;

@Service
public class AccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

  @Autowired
  private AccountMulticastBackpressureEventBus eventBus;

  @Autowired
  private AccountPostgresRepository repository;

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private TopicExchange topicExchange;

  public Collection<AccountDto> getAccounts() {
    return repository.findAll().stream().map(AccountEntity::toDto).toList();
  }

  public Optional<AccountDto> getAccount(UUID id) {
    return repository.findById(id).map(AccountEntity::toDto);
  }

  public AccountDto addAccount(String firstName, String lastName, String email) {
    AccountEntity account = new AccountEntity(firstName, lastName, email);
    AccountDto persistedDto = repository.save(account).toDto();
    send(topicExchange, AccountRoutingKey.ADDED, persistedDto);
    return persistedDto;
  }

  public Optional<AccountDto> deleteAccount(UUID id) {
    Optional<AccountDto> account = getAccount(id);
    if (account.isPresent()) {
      repository.deleteById(id);
      send(topicExchange, AccountRoutingKey.DELETED, account.get());
    }
    return account;
  }

  public Publisher<AccountDto> listen(AccountEvent event) {
    return eventBus.subscribe(Optional.empty(), Optional.of(event));
  }

  private void send(Exchange exchange, String routingKey, AccountDto account) {
    template.convertAndSend(exchange.getName(), routingKey, account);
  }

  @RabbitListener(queues = "#{accountAddedQueue.name}")
  public void addedListener(AccountDto account) {
    LOGGER.info("Added a account! Content: {}", account);
    Sinks.EmitResult result = eventBus.publish(account, AccountEvent.ADDED);
    LOGGER.info("EmitResult: {}", result);
  }

  @RabbitListener(queues = "#{accountDeletedQueue.name}")
  public void deletedListener(AccountDto account) {
    LOGGER.info("Deleted a account! Content: {}", account.getId(), account);
    Sinks.EmitResult result = eventBus.publish(account, AccountEvent.DELETED);
    LOGGER.info("EmitResult: {}", result);
  }

}
