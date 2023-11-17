package com.github.dannil.micro.personservice.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PersonDto implements Messageable<UUID> {

  private UUID id;
  private String firstName;
  private String lastName;

  @Override
  public UUID messageId() {
    return id;
  }

}
