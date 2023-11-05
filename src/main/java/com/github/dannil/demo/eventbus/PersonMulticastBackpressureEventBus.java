package com.github.dannil.demo.eventbus;

import com.github.dannil.demo.model.PersonDto;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.Objects;
import java.util.UUID;

@Service
public class PersonMulticastBackpressureEventBus implements EventBus<UUID, PersonDto> {

    private Sinks.Many<PersonDto> sink;
    private Flux<PersonDto> flux;

    public PersonMulticastBackpressureEventBus() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
        this.flux = sink.asFlux();
    }

    public Sinks.EmitResult publish(UUID key, PersonDto value) {
        return sink.tryEmitNext(value);
    }

    public Publisher<PersonDto> subscribe() {
        return flux;
    }

    public Publisher<PersonDto> subscribe(UUID key) {
        return flux.filter(p -> Objects.equals(key, p.getId()));
    }

}
