package com.github.dannil.demo.resolver;

import com.github.dannil.demo.model.Address;
import com.github.dannil.demo.model.Person;
import com.github.dannil.demo.service.PersonService;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PersonResolver {

    private PersonService personService;

    @QueryMapping
    public Flux<Person> persons(@Argument Optional<String> id) {
        return Mono.just(id)
                .switchIfEmpty(personService::getPersons)
                .flatMap(id -> personService::getPerson)
                .defaultIfEmpty(m -> personService.getPersons());
    }

    @MutationMapping
    public Mono<Person> addPerson(@Argument String id, @Argument String firstName, @Argument String lastName, @Argument Address address) {
        return personService.addPerson(id, firstName, lastName, address);
    }

    @MutationMapping
    public Mono<Person> deletePerson(@Argument String id) {
        return personService.deletePerson(id);
    }

    @SubscriptionMapping
    public Publisher<Person> personSubscription(@Argument String id) {
        if (id == null) {
            return personService.notifyChange();
        }
        return personService.notifyChange(id);
    }

}
