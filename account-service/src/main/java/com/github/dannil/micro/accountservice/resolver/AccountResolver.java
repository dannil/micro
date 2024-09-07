package com.github.dannil.micro.accountservice.resolver;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.github.dannil.micro.accountservice.auth.IsResourceOwner;
import com.github.dannil.micro.accountservice.configuration.AccountEvent;
import com.github.dannil.micro.accountservice.model.AccountDto;
import com.github.dannil.micro.accountservice.service.AccountService;

import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class AccountResolver {

  private AccountService accountService;

  @QueryMapping
  public Collection<AccountDto> accounts(@Argument Optional<UUID> id) {
    if (id.isPresent()) {
      Optional<AccountDto> account = accountService.getAccount(id.get());
      return account.map(List::of).orElseGet(List::of);
    }
    return accountService.getAccounts();
  }

  @QueryMapping
  @IsResourceOwner
  public AccountDto me(@AuthenticationPrincipal JwtAuthenticationToken auth,
      @Argument UUID id) {
    return accountService.getAccount(id).orElseThrow(IllegalArgumentException::new);
  }

  @MutationMapping
  public AccountDto addAccount(@Argument String firstName, @Argument String lastName, @Argument String email) {
    return accountService.addAccount(firstName, lastName, email);
  }

  @MutationMapping
  public Optional<AccountDto> deleteAccount(@Argument UUID id) {
    return accountService.deleteAccount(id);
  }

  @SubscriptionMapping
  public Publisher<AccountDto> accountSubscription(@Argument Optional<AccountEvent> event) {
    return accountService.listen(event.orElse(AccountEvent.ALL));
  }

}
