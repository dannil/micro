package com.github.dannil.demo.service;

import com.github.dannil.demo.model.Address;
import com.github.dannil.demo.model.Person;
import com.github.dannil.demo.eventbus.PersonMulticastBackpressureEventBus;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.*;

@Service
public class PersonService {

    @Autowired
    private PersonMulticastBackpressureEventBus pubsub;

    private Map<String, Person> persons;

    public PersonService() {
        persons = new HashMap<>();
        persons.put("1", new Person("1", "Bob", "Ferguson", new Address("Baker Street 45", "175-42")));
        persons.put("2", new Person("2", "Alice", "Matthews", new Address("Diagonal Alley", "13 BEF-97")));
    }

    public Collection<Person> getPersons() {
        return persons.values();
    }

    public Person getPerson(String id) {
        return persons.get(id);
    }

    public Person addPerson(String id, String firstName, String lastName, Address address) {
        Person person = new Person(id, firstName, lastName, address);
        persons.put(id, person);
        Sinks.EmitResult result = pubsub.publish(id, person);
        return person;
    }

    public Optional<Person> deletePerson(String id) {
        Optional<Person> person = Optional.ofNullable(persons.get(id));
        if (person.isPresent()) {
            persons.remove(id);
            Sinks.EmitResult result = pubsub.publish(id, person.get());
        }
        return person;
    }

    public Publisher<Person> notifyChange() {
        return pubsub.subscribe();
    }

    public Publisher<Person> notifyChange(String id) {
        return pubsub.subscribe(id);
    }

}
