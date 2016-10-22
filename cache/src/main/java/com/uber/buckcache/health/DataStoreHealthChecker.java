package com.uber.buckcache.health;

import com.codahale.metrics.health.HealthCheck;
import com.uber.buckcache.datastore.DataStoreProvider;

public class DataStoreHealthChecker extends HealthCheck {
  private final DataStoreProvider dataStoreProvider;

  public DataStoreHealthChecker(DataStoreProvider dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public HealthCheck.Result check() throws Exception {
    return dataStoreProvider.check();
  }
}
