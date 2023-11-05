package com.github.dannil.demo.repository;

import com.github.dannil.demo.model.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonPostgresRepository extends JpaRepository<PersonEntity, UUID> {

}
