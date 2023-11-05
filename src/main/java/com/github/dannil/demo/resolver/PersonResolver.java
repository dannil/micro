package com.github.dannil.demo.resolver;

import com.github.dannil.demo.model.PersonDto;
import com.github.dannil.demo.service.PersonService;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class PersonResolver {

    private PersonService personService;

    @QueryMapping
    public Collection<PersonDto> persons(@Argument Optional<UUID> id) {
        if (id.isPresent()) {
            return List.of(personService.getPerson(id.get()).orElse(null));
        }
        return personService.getPersons();
    }

    @MutationMapping
    public PersonDto addPerson(@Argument String firstName, @Argument String lastName) {
        return personService.addPerson(firstName, lastName);
    }

    @MutationMapping
    public Optional<PersonDto> deletePerson(@Argument UUID id) {
        return personService.deletePerson(id);
    }

    @SubscriptionMapping
    public Publisher<PersonDto> personSubscription(@Argument UUID id) {
        if (id == null) {
            return personService.notifyChange();
        }
        return personService.notifyChange(id);
    }

}
