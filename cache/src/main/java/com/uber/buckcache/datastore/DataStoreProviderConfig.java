package com.uber.buckcache.datastore;

import java.util.Map;

public class DataStoreProviderConfig {

  private Map<String, Object> config;

  public Object getConfigValueForKey(String key) {
    return config.get(key);
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Object> config) {
    this.config = config;
  }

}
