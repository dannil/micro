package com.github.dannil.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class PersonDto {

    private UUID id;
    private String firstName;
    private String lastName;

}
