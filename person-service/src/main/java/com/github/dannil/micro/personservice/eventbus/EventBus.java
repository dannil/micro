package com.github.dannil.micro.personservice.eventbus;

import java.util.Optional;

import com.github.dannil.micro.personservice.model.Messageable;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Sinks;

public interface EventBus<K, V extends Messageable<K>, E> {
  Sinks.EmitResult publish(V value, E event);

  Publisher<V> subscribe(Optional<K> key, Optional<E> event);
}
