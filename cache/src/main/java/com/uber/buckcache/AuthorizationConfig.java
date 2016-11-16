package com.uber.buckcache;

import java.util.List;
import javax.annotation.Nonnull;

public class AuthorizationConfig {
  @Nonnull
  private List<String> authorizedTokens;

  public List<String> getAuthorizedTokens() {
    return authorizedTokens;
  }

  public void setAuthorizedTokens(List<String> authorizedTokens) {
    this.authorizedTokens = authorizedTokens;
  }
}
