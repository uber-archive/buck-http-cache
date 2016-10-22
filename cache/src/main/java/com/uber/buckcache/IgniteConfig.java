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
