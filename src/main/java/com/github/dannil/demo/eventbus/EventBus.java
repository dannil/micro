package com.github.dannil.demo.eventbus;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Sinks;

public interface EventBus<K, V> {
    Sinks.EmitResult publish(K key, V value);
    Publisher<V> subscribe();
    Publisher<V> subscribe(K key);
}
