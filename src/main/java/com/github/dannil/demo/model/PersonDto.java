package com.github.dannil.demo.model;

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
