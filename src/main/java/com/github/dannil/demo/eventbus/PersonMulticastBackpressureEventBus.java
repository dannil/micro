package com.github.dannil.demo.eventbus;

import com.github.dannil.demo.model.Person;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PersonMulticastBackpressureEventBus implements EventBus<String, Person> {

    private Sinks.Many<Person> sink;
    private Flux<Person> flux;

    public PersonMulticastBackpressureEventBus() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
        this.flux = sink.asFlux();
    }

    public Mono<Sinks.EmitResult> publish(String key, Person value) {
        return Mono.just(sink.tryEmitNext(value));
    }

    public Publisher<Person> subscribe() {
        return flux;
    }

    public Publisher<Person> subscribe(String key) {
        return flux.filter(p -> Objects.equals(key, p.getId()));
    }

}
