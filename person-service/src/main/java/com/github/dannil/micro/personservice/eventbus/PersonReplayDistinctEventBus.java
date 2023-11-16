package com.github.dannil.micro.personservice.eventbus;

import java.util.Objects;
import java.util.UUID;

import com.github.dannil.micro.personservice.model.PersonDto;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class PersonReplayDistinctEventBus implements EventBus<UUID, PersonDto> {

  private Sinks.Many<PersonDto> sink;
  private Flux<PersonDto> flux;

  public PersonReplayDistinctEventBus() {
    this.sink = Sinks.many().replay().all();
    this.flux = sink.asFlux().distinct(PersonDto::getId);
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
