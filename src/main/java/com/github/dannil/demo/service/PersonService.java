package com.github.dannil.demo.service;

import com.github.dannil.demo.model.Address;
import com.github.dannil.demo.model.Person;
import com.github.dannil.demo.publisher.PersonPublisher;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonPublisher publisher;

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
        publisher.publish(person);
        return person;
    }

    public Optional<Person> deletePerson(String id) {
        Optional<Person> person = Optional.ofNullable(persons.get(id));
        if (person.isPresent()) {
            persons.remove(id);
            publisher.publish(person.get());
        }
        return person;
    }

    public Publisher<Person> notifyChange() {
        return publisher.subscribe();
    }

    public Publisher<Person> notifyChange(String id) {
        return publisher.subscribe(id);
    }

}
