package com.github.dannil.demo.service;

import com.github.dannil.demo.model.PersonEntity;
import com.github.dannil.demo.eventbus.PersonMulticastBackpressureEventBus;
import com.github.dannil.demo.model.PersonDto;
import com.github.dannil.demo.repository.PersonPostgresRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.*;

@Service
public class PersonService {

    @Autowired
    private PersonMulticastBackpressureEventBus pubsub;

    @Autowired
    private PersonPostgresRepository repository;

    public PersonService() {

    }

    public Collection<PersonDto> getPersons() {
        return repository.findAll().stream().map(e -> e.toDto()).toList();
    }

    public Optional<PersonDto> getPerson(UUID id) {
        return repository.findById(id).map(e -> e.toDto());
    }

    public PersonDto addPerson(String firstName, String lastName) {
        PersonEntity person = new PersonEntity(firstName, lastName);
        PersonDto persistedDto = repository.save(person).toDto();
        Sinks.EmitResult result = pubsub.publish(persistedDto.getId(), persistedDto);
        return persistedDto;
    }

    public Optional<PersonDto> deletePerson(UUID id) {
        Optional<PersonDto> person = getPerson(id);
        if (person.isPresent()) {
            repository.deleteById(id);
            Sinks.EmitResult result = pubsub.publish(id, person.get());
        }
        return person;
    }

    public Publisher<PersonDto> notifyChange() {
        return pubsub.subscribe();
    }

    public Publisher<PersonDto> notifyChange(UUID id) {
        return pubsub.subscribe(id);
    }

}
