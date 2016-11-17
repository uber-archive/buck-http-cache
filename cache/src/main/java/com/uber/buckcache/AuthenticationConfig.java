package com.uber.buckcache;

import java.util.List;
import javax.annotation.Nonnull;

public class AuthenticationConfig {
  @Nonnull
  private List<String> authenticatedTokens;

  public List<String> getAuthenticatedTokens() {
    return authenticatedTokens;
  }

  public void setAuthenticatedTokens(List<String> authenticatedTokens) {
    this.authenticatedTokens = authenticatedTokens;
  }
}
