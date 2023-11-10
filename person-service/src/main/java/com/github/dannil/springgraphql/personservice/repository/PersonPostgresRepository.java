package com.github.dannil.springgraphql.personservice.repository;

import com.github.dannil.springgraphql.personservice.model.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonPostgresRepository extends JpaRepository<PersonEntity, UUID> {

}
