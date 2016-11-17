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

  protected List<String> authorizedTokens;

  protected HttpHeaderAuthFilter() {}
  public HttpHeaderAuthFilter(List<String> authorizedTokens) {
    this.authorizedTokens = authorizedTokens;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    if (authorizedTokens == null) {
      return ;
    }

    String token = requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (token == null || !authorizedTokens.contains(token)) {
      throw new NotAuthorizedException("Unauthorized");
    }
  }
}
