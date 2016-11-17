package com.uber.buckcache;

import java.util.List;
import javax.annotation.Nonnull;

public class AuthenticationConfig {
  @Nonnull
  private List<String> tokens;

  public List<String> getTokens() {
    return tokens;
  }

  public void setTokens(List<String> tokens) { this.tokens = tokens; }
}
