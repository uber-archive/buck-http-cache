package com.uber.buckcache.auth;

import io.dropwizard.auth.AuthFilter;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptyList;

@Priority(Priorities.AUTHENTICATION)
public class HttpHeaderAuthFilter extends AuthFilter {

  private final List<String> tokens;

  public HttpHeaderAuthFilter() {
    this(emptyList());
  }

  public HttpHeaderAuthFilter(List<String> tokens) {
    this.tokens = tokens == null ? emptyList() : tokens;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    if (tokens.isEmpty()) {
      return ;
    }

    String token = requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (token == null || !tokens.contains(token)) {
      throw new NotAuthorizedException("Unauthorized");
    }
  }
}
