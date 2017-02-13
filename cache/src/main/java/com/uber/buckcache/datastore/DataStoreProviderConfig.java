package com.uber.buckcache.datastore;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataStoreProviderConfig {

  private final Map<String, Object> config;

  @JsonCreator
  public DataStoreProviderConfig(@JsonProperty("config") Map<String, Object> config) {
    this.config = config;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

}
