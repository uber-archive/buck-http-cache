package com.uber.buckcache;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uber.buckcache.datastore.DataStoreProviderConfig;

import io.dropwizard.Configuration;

public final class BuckCacheConfiguration extends Configuration {

  @Nonnull
  private final CacheInstanceMode mode;
  @Nonnull
  private final StatsdConfig statsd;
  @Nonnull
  private final String throttleLimit;
  @Nonnull
  private final String storeProviderKlass;
  @Nonnull
  private final DataStoreProviderConfig storeProviderConfig;
  @Nonnull
  private final AuthenticationConfig authenticationConfig;

  @JsonCreator
  public BuckCacheConfiguration(
      @Nonnull @JsonProperty("mode") CacheInstanceMode mode,
      @Nonnull @JsonProperty("statsd") StatsdConfig statsd,
      @Nonnull @JsonProperty("throttleLimit") String throttleLimit,
      @Nonnull @JsonProperty("storeProviderKlass") String storeProviderKlass,
      @Nonnull @JsonProperty("storeProviderConfig") DataStoreProviderConfig storeProviderConfig,
      @Nonnull @JsonProperty("authenticationConfig") AuthenticationConfig authenticationConfig) {
    this.mode = mode;
    this.statsd = statsd;
    this.throttleLimit = throttleLimit;
    this.storeProviderKlass = storeProviderKlass;
    this.storeProviderConfig = storeProviderConfig;
    this.authenticationConfig = authenticationConfig;
  }

  public String getStoreProviderKlass() {
    return storeProviderKlass;
  }

  public DataStoreProviderConfig getStoreProviderConfig() {
    return storeProviderConfig;
  }

  public AuthenticationConfig getAuthenticationConfig() {
    return authenticationConfig;
  }

  public String getThrottleLimit() {
    return throttleLimit;
  }

  public StatsdConfig getStatsd() {
    return statsd;
  }

  public CacheInstanceMode getMode() {
    return mode;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
