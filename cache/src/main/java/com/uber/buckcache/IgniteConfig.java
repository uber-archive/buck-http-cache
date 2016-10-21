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

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.ignite.cache.CacheMode;

public class IgniteConfig {

  @Nonnull
  private String multicastIP;
  @Nonnull
  private Integer multicastPort;
  @Nonnull
  private CacheMode cacheMode;
  @Nonnull
  private Integer cacheBackupCount;
  @Nonnull
  private TimeUnit expirationTimeUnit;
  @Nonnull
  private Long expirationTimeValue;
  @Nonnull
  private Integer atomicSequencereserveSize;
  @Nonnull
  private String offHeapStorageSize;
  @Nonnull
  private List<String> hostIPs;

  public List<String> getHostIPs() {
    return hostIPs;
  }

  public void setHostIPs(List<String> hostIPs) {
    this.hostIPs = hostIPs;
  }

  public String getOffHeapStorageSize() {
    return offHeapStorageSize;
  }

  public void setOffHeapStorageSize(String offHeapStorageSize) {
    this.offHeapStorageSize = offHeapStorageSize;
  }

  public String getMulticastIP() {
    return multicastIP;
  }

  public void setMulticastIP(String multicastIP) {
    this.multicastIP = multicastIP;
  }

  public Integer getMulticastPort() {
    return multicastPort;
  }

  public void setMulticastPort(Integer multicastPort) {
    this.multicastPort = multicastPort;
  }

  public CacheMode getCacheMode() {
    return cacheMode;
  }

  public void setCacheMode(CacheMode cacheMode) {
    this.cacheMode = cacheMode;
  }

  public Integer getCacheBackupCount() {
    return cacheBackupCount;
  }

  public void setCacheBackupCount(Integer cacheBackupCount) {
    this.cacheBackupCount = cacheBackupCount;
  }

  public TimeUnit getExpirationTimeUnit() {
    return expirationTimeUnit;
  }

  public void setExpirationTimeUnit(TimeUnit expirationTimeUnit) {
    this.expirationTimeUnit = expirationTimeUnit;
  }

  public Long getExpirationTimeValue() {
    return expirationTimeValue;
  }

  public void setExpirationTimeValue(Long expirationTimeValue) {
    this.expirationTimeValue = expirationTimeValue;
  }

  public Integer getAtomicSequencereserveSize() {
    return atomicSequencereserveSize;
  }

  public void setAtomicSequencereserveSize(Integer atomicSequencereserveSize) {
    this.atomicSequencereserveSize = atomicSequencereserveSize;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
