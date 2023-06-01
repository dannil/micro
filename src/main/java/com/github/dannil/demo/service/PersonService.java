package com.github.dannil.demo.service;

import com.github.dannil.demo.configuration.KafkaConfiguration;
import com.github.dannil.demo.eventbus.PersonMulticastBackpressureEventBus;
import com.github.dannil.demo.model.PersonDto;
import com.github.dannil.demo.model.PersonEntity;
import com.github.dannil.demo.repository.PersonPostgresRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.*;

@Service
public class PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonMulticastBackpressureEventBus pubsub;

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
        send(persistedDto);
        return persistedDto;
    }

    public Optional<PersonDto> deletePerson(UUID id) {
        Optional<PersonDto> person = getPerson(id);
        if (person.isPresent()) {
            repository.deleteById(id);
            send(person.get());
        }
        return person;
    }

    public Publisher<PersonDto> listen() {
        return pubsub.subscribe();
    }

    private void send(PersonDto person) {
        kafkaTemplate.send(KafkaConfiguration.PERSONS_TOPIC, person);
        kafkaTemplate.flush();
    }

    @KafkaListener(topics = KafkaConfiguration.PERSONS_TOPIC)
    private void consumer(PersonDto person) {
        Sinks.EmitResult result = pubsub.publish(person.getId(), person);
        LOGGER.info("Content: {}, result: {}", person, result);
    }

}
