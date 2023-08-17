package com.github.dannil.demo.publisher;

import com.github.dannil.demo.model.Person;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.Objects;

@Service
public class PersonPublisher {

    private Sinks.Many<Person> sink;
    private Flux<Person> flux;

    public PersonPublisher() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
        this.flux = sink.asFlux();
    }

    public void publish(Person p) {
        sink.tryEmitNext(p);
    }

    public Publisher<Person> subscribe() {
        return flux;
    }

    public Publisher<Person> subscribe(String id) {
        return flux.filter(p -> Objects.equals(id, p.getId()));
    }

}
