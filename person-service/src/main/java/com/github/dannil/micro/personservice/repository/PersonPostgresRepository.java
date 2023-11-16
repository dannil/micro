package com.github.dannil.micro.personservice.repository;

import java.util.UUID;

import com.github.dannil.micro.personservice.model.PersonEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonPostgresRepository extends JpaRepository<PersonEntity, UUID> {

}
