package com.uber.buckcache;

import java.util.List;
import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationConfig {
  @Nonnull
  private final List<String> tokens;

  @JsonCreator
  public AuthenticationConfig(@Nonnull @JsonProperty("tokens") List<String> tokens) {
    this.tokens = tokens;
  }

  public List<String> getTokens() {
    return tokens;
  }

}
