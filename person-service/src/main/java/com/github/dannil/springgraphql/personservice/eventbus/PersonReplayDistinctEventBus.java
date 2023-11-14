package com.github.dannil.springgraphql.personservice.eventbus;

import com.github.dannil.springgraphql.personservice.model.PersonDto;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;

@Service
public class PersonReplayDistinctEventBus implements EventBus<String, PersonDto> {

  private Sinks.Many<PersonDto> sink;
  private Flux<PersonDto> flux;

  public PersonReplayDistinctEventBus() {
    this.sink = Sinks.many().replay().all();
    this.flux = sink.asFlux().distinct(PersonDto::getId);
  }

  public Sinks.EmitResult publish(String key, PersonDto value) {
    return sink.tryEmitNext(value);
  }

  public Publisher<PersonDto> subscribe() {
    return flux;
  }

  public Publisher<PersonDto> subscribe(String key) {
    return flux.filter(p -> Objects.equals(key, p.getId()));
  }

}
