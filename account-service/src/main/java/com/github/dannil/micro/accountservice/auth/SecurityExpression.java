package com.github.dannil.micro.accountservice.auth;

import com.github.dannil.micro.accountservice.model.AccountDto;
import com.github.dannil.micro.accountservice.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SecurityExpression {

  private AccountService accountService;

  public boolean isResourceOwner(Authentication authentication, UUID id) {
    if (authentication instanceof JwtAuthenticationToken authenticationToken) {
      Optional<AccountDto> accountDto = accountService.getAccount(id);
      if (accountDto.isEmpty()) {
        return false;
      }
      String jwtEmailClaim = authenticationToken.getToken().getClaim("email");
      return Objects.equals(jwtEmailClaim, accountDto.get().getEmail());
    }
    return false;
  }

}
