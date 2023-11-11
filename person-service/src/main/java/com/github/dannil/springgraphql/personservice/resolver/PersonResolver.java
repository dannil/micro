package com.github.dannil.springgraphql.personservice.resolver;

import com.github.dannil.springgraphql.personservice.configuration.PersonEvent;
import com.github.dannil.springgraphql.personservice.model.PersonDto;
import com.github.dannil.springgraphql.personservice.service.PersonService;
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
            Optional<PersonDto> person = personService.getPerson(id.get());
            return person.map(List::of).orElseGet(List::of);
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
    public Publisher<PersonDto> personSubscription() {
        return personService.listen(PersonEvent.ALL);
    }

    @SubscriptionMapping
    public Publisher<PersonDto> personAddedSubscription() {
        return personService.listen(PersonEvent.ADDED);
    }

    @SubscriptionMapping
    public Publisher<PersonDto> personDeletedSubscription() {
        return personService.listen(PersonEvent.DELETED);
    }

}
