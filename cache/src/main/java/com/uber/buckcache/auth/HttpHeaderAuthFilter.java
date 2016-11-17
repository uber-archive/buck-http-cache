package com.uber.buckcache.auth;

import io.dropwizard.auth.AuthFilter;

import java.io.IOException;
import java.util.List;
import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

@Priority(Priorities.AUTHENTICATION)
public class HttpHeaderAuthFilter extends AuthFilter {

  protected List<String> authenticatedTokens;

  protected HttpHeaderAuthFilter() {}
  public HttpHeaderAuthFilter(List<String> authenticatedTokens) {
    this.authenticatedTokens = authenticatedTokens;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    if (authenticatedTokens == null) {
      return ;
    }

    String token = requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (token == null || !authenticatedTokens.contains(token)) {
      throw new NotAuthorizedException("Unauthorized");
    }
  }
}
