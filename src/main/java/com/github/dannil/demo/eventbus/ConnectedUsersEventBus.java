package com.github.dannil.demo.eventbus;

import com.github.dannil.demo.model.Person;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ConnectedUsersEventBus implements EventBus<UUID, UUID> {

    private Sinks.Many<UUID> sink;
    private Flux<UUID> flux;

    public ConnectedUsersEventBus() {
        this.sink = Sinks.many().replay().all();
        this.flux = sink.asFlux().distinct(UUID::hashCode);
    }

    public Sinks.EmitResult publish(UUID key, UUID value) {
        return sink.tryEmitNext(value);
    }

    public Publisher<UUID> subscribe() {
        return flux;
    }

    public Publisher<UUID> subscribe(UUID key) {
        return flux.filter(u -> Objects.equals(key, u));
    }

}
