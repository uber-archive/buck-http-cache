/**
 * Copyright (c) 2016 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
