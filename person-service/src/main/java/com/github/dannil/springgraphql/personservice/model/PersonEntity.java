package com.github.dannil.springgraphql.personservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "person")
public class PersonEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NonNull
  private String firstName;

  @NonNull
  private String lastName;

  // For JPA only, no use
  public PersonEntity() {

  }

  public PersonDto toDto() {
    return new PersonDto(id, firstName, lastName);
  }
}
