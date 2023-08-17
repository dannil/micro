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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PersonResolver {

    private PersonService personService;

    @QueryMapping
    public Collection<Person> persons(@Argument Optional<String> id) {
        if (id.isPresent()) {
            return List.of(personService.getPerson(id.get()));
        }
        return personService.getPersons();
    }

    @MutationMapping
    public Person addPerson(@Argument String id, @Argument String firstName, @Argument String lastName, @Argument Address address) {
        return personService.addPerson(id, firstName, lastName, address);
    }

    @MutationMapping
    public Optional<Person> deletePerson(@Argument String id) {
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
