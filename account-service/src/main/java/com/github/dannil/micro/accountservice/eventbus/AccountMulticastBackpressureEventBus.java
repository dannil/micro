package com.github.dannil.micro.accountservice.eventbus;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.github.dannil.micro.accountservice.configuration.AccountEvent;
import com.github.dannil.micro.accountservice.model.AccountDto;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Service
public class AccountMulticastBackpressureEventBus implements EventBus<UUID, AccountDto, AccountEvent> {

  private Sinks.Many<AccountEventBusMessage> sink;

  public AccountMulticastBackpressureEventBus() {
    this.sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
  }

  public Sinks.EmitResult publish(AccountDto value, AccountEvent event) {
    return sink.tryEmitNext(new AccountEventBusMessage(value.messageId(), value, event));
  }

  public Publisher<AccountDto> subscribe(Optional<UUID> key, Optional<AccountEvent> event) {
    var flux = sink.asFlux();
    if (key.isPresent()) {
      flux = flux.filter(p -> Objects.equals(p.getId(), key.get()));
    }
    if (event.isPresent()) {
      var eventValue = event.get();
      if (eventValue != AccountEvent.ALL) {
        flux = flux.filter(p -> Objects.equals(p.getEvent(), event.get()));
      }
    }
    return flux.map(AccountEventBusMessage::getContent);
  }

  @AllArgsConstructor
  @Getter
  private class AccountEventBusMessage {

    private UUID id;
    private AccountDto content;
    private AccountEvent event;

  }

}
