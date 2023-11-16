package com.github.dannil.micro.personservice.model;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PersonDto {

  private UUID id;
  private String firstName;
  private String lastName;

}