package com.uber.buckcache.utils;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.uber.buckcache.StatsdConfig;

public class StatsDClient {
  private static StatsDClient statsDClient;

  private final StatsdConfig statsDConfig;
  private final com.timgroup.statsd.StatsDClient underlyingClient;

  private StatsDClient(StatsdConfig statsDConfig) {
    this.statsDConfig = statsDConfig;

    if (this.statsDConfig.isEnabled()) {
      underlyingClient = new NonBlockingStatsDClient(this.statsDConfig.getPrefix(), this.statsDConfig.getHost(),
          this.statsDConfig.getPort());
    } else {
      underlyingClient = new NoOpStatsDClient();
    }
  }

  public synchronized static void init(StatsdConfig statsDConfig) {
    if (statsDClient == null) {
      statsDClient = new StatsDClient(statsDConfig);
    }
  }

  public static StatsDClient get() {
    return statsDClient;
  }

  public void count(String metricName, long countValue) {
    underlyingClient.count(metricName, countValue, statsDConfig.getSampleRate());
  }

  public void recordExecutionTime(String metricName, long timeDifferenceInMillis) {
    underlyingClient.recordExecutionTime(metricName, timeDifferenceInMillis, statsDConfig.getSampleRate());
  }

  public void recordExecutionTimeToNow(String metricName, long startTime) {
    underlyingClient.recordExecutionTime(metricName, System.currentTimeMillis() - startTime,
        statsDConfig.getSampleRate());
  }
  
  public com.timgroup.statsd.StatsDClient getUnderlying() {
    return underlyingClient;
  }
}
