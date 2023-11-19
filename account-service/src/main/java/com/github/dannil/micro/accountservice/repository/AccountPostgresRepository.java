package com.github.dannil.micro.accountservice.repository;

import java.util.UUID;

import com.github.dannil.micro.accountservice.model.AccountEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountPostgresRepository extends JpaRepository<AccountEntity, UUID> {

}
