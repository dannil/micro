package com.github.dannil.micro.accountservice.configuration.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GraphiQLOriginFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String requestUrl = request.getRequestURL().toString();
    String referer = request.getHeader("Referer");
    System.out.println(referer);
    String prio = Objects.nonNull(referer) ? referer : requestUrl;
    if (prio != null && prio.contains("graphiql")) {
      System.out.println("blabla");
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

}
