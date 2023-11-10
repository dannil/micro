package com.github.dannil.springgraphql.personservice.service;

import com.github.dannil.springgraphql.personservice.configuration.kafka.PersonEvent;
import com.github.dannil.springgraphql.personservice.configuration.kafka.PersonTopic;
import com.github.dannil.springgraphql.personservice.eventbus.PersonMulticastBackpressureEventBus;
import com.github.dannil.springgraphql.personservice.model.PersonDto;
import com.github.dannil.springgraphql.personservice.model.PersonEntity;
import com.github.dannil.springgraphql.personservice.repository.PersonPostgresRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;

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
    private KafkaTemplate<String, PersonDto> kafkaTemplate;

    public Collection<PersonDto> getPersons() {
        return repository.findAll().stream().map(PersonEntity::toDto).toList();
    }

    public Optional<PersonDto> getPerson(UUID id) {
        return repository.findById(id).map(PersonEntity::toDto);
    }

    public PersonDto addPerson(String firstName, String lastName) {
        PersonEntity person = new PersonEntity(firstName, lastName);
        PersonDto persistedDto = repository.save(person).toDto();
        send(persistedDto, PersonTopic.ADDED);
        return persistedDto;
    }

    public Optional<PersonDto> deletePerson(UUID id) {
        Optional<PersonDto> person = getPerson(id);
        if (person.isPresent()) {
            repository.deleteById(id);
            send(person.get(), PersonTopic.DELETED);
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

    private void send(PersonDto person, String topic) {
        kafkaTemplate.send(topic, person);
        kafkaTemplate.flush();
    }

    @KafkaListener(topics = PersonTopic.ADDED)
    private void addedConsumer(PersonDto person) {
        Sinks.EmitResult result = addedEventBus.publish(person.getId(), person);
        LOGGER.info("Added a person! Content: {}, result: {}", person, result);
    }

    @KafkaListener(topics = PersonTopic.DELETED)
    private void deletedConsumer(PersonDto person) {
        Sinks.EmitResult result = deletedEventBus.publish(person.getId(), person);
        LOGGER.info("Deleted a person! Content: {}, result: {}", person, result);
    }

}
