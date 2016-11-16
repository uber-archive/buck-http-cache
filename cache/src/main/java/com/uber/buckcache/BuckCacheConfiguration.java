package com.uber.buckcache;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uber.buckcache.datastore.DataStoreProviderConfig;

import io.dropwizard.Configuration;

public final class BuckCacheConfiguration extends Configuration {

  @Nonnull
  private CacheInstanceMode mode;
  @Nonnull
  private StatsdConfig statsd;
  @Nonnull
  private String throttleLimit;
  @Nonnull
  private String storeProviderKlass;
  @Nonnull
  private DataStoreProviderConfig storeProviderConfig;
  @Nonnull
  private AuthorizationConfig authorizationConfig;

  public String getStoreProviderKlass() {
    return storeProviderKlass;
  }

  public void setStoreProviderKlass(String storeProviderKlass) {
    this.storeProviderKlass = storeProviderKlass;
  }

  public DataStoreProviderConfig getStoreProviderConfig() {
    return storeProviderConfig;
  }

  public void setStoreProviderConfig(DataStoreProviderConfig storeProviderConfig) {
    this.storeProviderConfig = storeProviderConfig;
  }

  public AuthorizationConfig getAuthorizationConfig() {
    return authorizationConfig;
  }

  public void setAuthorizationConfig(AuthorizationConfig authorizationConfig) {
    this.authorizationConfig = authorizationConfig;
  }

  public String getThrottleLimit() {
    return throttleLimit;
  }

  public void setThrottleLimit(String throttleLimit) {
    this.throttleLimit = throttleLimit;
  }

  public StatsdConfig getStatsd() {
    return statsd;
  }

  public void setStatsd(StatsdConfig statsd) {
    this.statsd = statsd;
  }

  @Nonnull
  @JsonProperty
  public CacheInstanceMode getMode() {
    return mode;
  }

  @JsonProperty
  @SuppressWarnings("unused")
  public void setMode(CacheInstanceMode mode) {
    this.mode = mode;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
