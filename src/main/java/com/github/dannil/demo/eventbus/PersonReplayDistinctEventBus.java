package com.github.dannil.demo.eventbus;

import com.github.dannil.demo.model.Person;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;

@Service
public class PersonReplayDistinctEventBus implements EventBus<String, Person> {

    private Sinks.Many<Person> sink;
    private Flux<Person> flux;

    public PersonReplayDistinctEventBus() {
        this.sink = Sinks.many().replay().all();
        this.flux = sink.asFlux().distinct(Person::getId);
    }

    public Sinks.EmitResult publish(String key, Person value) {
        return sink.tryEmitNext(value);
    }

    public Publisher<Person> subscribe() {
        return flux;
    }

    public Publisher<Person> subscribe(String key) {
        return flux.filter(p -> Objects.equals(key, p.getId()));
    }

}
