package com.github.dannil.micro.personservice.eventbus;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.github.dannil.micro.personservice.configuration.PersonEvent;
import com.github.dannil.micro.personservice.model.PersonDto;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Service
public class PersonMulticastBackpressureEventBus implements EventBus<UUID, PersonDto, PersonEvent> {

  private Sinks.Many<PersonEventBusMessage> sink;

  public PersonMulticastBackpressureEventBus() {
    this.sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
  }

  public Sinks.EmitResult publish(PersonDto value, PersonEvent event) {
    return sink.tryEmitNext(new PersonEventBusMessage(value.messageId(), value, event));
  }

  public Publisher<PersonDto> subscribe(Optional<UUID> key, Optional<PersonEvent> event) {
    var flux = sink.asFlux();
    if (key.isPresent()) {
      flux = flux.filter(p -> Objects.equals(p.getId(), key.get()));
    }
    if (event.isPresent()) {
      var eventValue = event.get();
      if (eventValue != PersonEvent.ALL) {
        flux = flux.filter(p -> Objects.equals(p.getEvent(), event.get()));
      }
    }
    return flux.map(PersonEventBusMessage::getContent);
  }

  @AllArgsConstructor
  @Getter
  private class PersonEventBusMessage {

    private UUID id;
    private PersonDto content;
    private PersonEvent event;

  }

}
