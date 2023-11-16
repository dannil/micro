package com.github.dannil.micro.personservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.dannil.micro.personservice.model.PersonEntity;

import java.util.UUID;

public interface PersonPostgresRepository extends JpaRepository<PersonEntity, UUID> {

}
