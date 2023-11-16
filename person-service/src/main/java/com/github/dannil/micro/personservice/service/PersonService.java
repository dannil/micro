package com.github.dannil.micro.personservice.service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.github.dannil.micro.personservice.configuration.PersonEvent;
import com.github.dannil.micro.personservice.configuration.PersonRoutingKey;
import com.github.dannil.micro.personservice.eventbus.PersonMulticastBackpressureEventBus;
import com.github.dannil.micro.personservice.model.PersonDto;
import com.github.dannil.micro.personservice.model.PersonEntity;
import com.github.dannil.micro.personservice.repository.PersonPostgresRepository;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class PersonService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

  @Autowired
  private PersonMulticastBackpressureEventBus addedEventBus;

  @Autowired
  private PersonMulticastBackpressureEventBus deletedEventBus;

  @Autowired
  private PersonPostgresRepository repository;

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private TopicExchange topicExchange;

  public Collection<PersonDto> getPersons() {
    return repository.findAll().stream().map(PersonEntity::toDto).toList();
  }

  public Optional<PersonDto> getPerson(UUID id) {
    return repository.findById(id).map(PersonEntity::toDto);
  }

  public PersonDto addPerson(String firstName, String lastName) {
    PersonEntity person = new PersonEntity(firstName, lastName);
    PersonDto persistedDto = repository.save(person).toDto();
    send(topicExchange, PersonRoutingKey.ADDED, persistedDto);
    return persistedDto;
  }

  public Optional<PersonDto> deletePerson(UUID id) {
    Optional<PersonDto> person = getPerson(id);
    if (person.isPresent()) {
      repository.deleteById(id);
      send(topicExchange, PersonRoutingKey.DELETED, person.get());
    }
    return person;
  }

  public Publisher<PersonDto> listen(PersonEvent event) {
    return switch (event) {
      case ADDED -> addedEventBus.subscribe();
      case DELETED -> deletedEventBus.subscribe();
      case ALL -> Flux.concat(addedEventBus.subscribe(), deletedEventBus.subscribe());
    };
  }

  private void send(Exchange exchange, String routingKey, PersonDto person) {
    template.convertAndSend(exchange.getName(), routingKey, person);
  }

  @RabbitListener(queues = "#{personAddedQueue.name}")
  public void addedListener(PersonDto person) {
    LOGGER.info("Added a person! Content: {}", person);
    Sinks.EmitResult result = addedEventBus.publish(person.getId(), person);
    LOGGER.info("EmitResult: {}", result);
  }

  @RabbitListener(queues = "#{personDeletedQueue.name}")
  public void deletedListener(PersonDto person) {
    LOGGER.info("Deleted a person! Content: {}", person);
    Sinks.EmitResult result = deletedEventBus.publish(person.getId(), person);
    LOGGER.info("EmitResult: {}", result);
  }

}
