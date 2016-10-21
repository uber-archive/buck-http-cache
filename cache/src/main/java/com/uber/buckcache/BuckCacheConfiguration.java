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
