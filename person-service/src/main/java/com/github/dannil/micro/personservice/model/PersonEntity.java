package com.github.dannil.micro.personservice.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
