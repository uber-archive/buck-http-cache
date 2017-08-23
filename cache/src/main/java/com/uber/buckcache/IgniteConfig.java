package com.uber.buckcache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.ignite.cache.CacheMemoryMode;
import org.apache.ignite.cache.CacheMode;

public class IgniteConfig {

  @Nonnull
  private final String multicastIP;
  @Nonnull
  private final Integer multicastPort;
  @Nonnull
  private final CacheMode cacheMode;
  @Nonnull
  private final Integer cacheBackupCount;
  @Nonnull
  private final TimeUnit expirationTimeUnit;
  @Nonnull
  private final Long expirationTimeValue;
  @Nonnull
  private final Integer atomicSequenceReserveSize;
  @Nonnull
  private final String offHeapStorageSize;
  @Nonnull
  private final List<String> hostIPs;
  @Nonnull
  private final String dnsLookupAddress;
  @Nonnull
  private final CacheMemoryMode cacheMemoryMode;

  @JsonCreator
  public IgniteConfig(
      @Nonnull @JsonProperty("multicastIP") String multicastIP,
      @Nonnull @JsonProperty("multicastPort") Integer multicastPort,
      @Nonnull @JsonProperty("cacheMode") CacheMode cacheMode,
      @Nonnull @JsonProperty("cacheBackupCount") Integer cacheBackupCount,
      @Nonnull @JsonProperty("expirationTimeUnit") TimeUnit expirationTimeUnit,
      @Nonnull @JsonProperty("expirationTimeValue") Long expirationTimeValue,
      @Nonnull @JsonProperty("atomicSequenceReserveSize") Integer atomicSequenceReserveSize,
      @Nonnull @JsonProperty("offHeapStorageSize") String offHeapStorageSize,
      @Nonnull @JsonProperty("hostIPs") List<String> hostIPs,
      @Nonnull @JsonProperty("dnsLookupAddress") String dnsLookupAddress,
      @Nonnull @JsonProperty("cacheMemoryMode") CacheMemoryMode cacheMemoryMode) {
    this.multicastIP = multicastIP;
    this.multicastPort = multicastPort;
    this.cacheMode = cacheMode;
    this.cacheBackupCount = cacheBackupCount;
    this.expirationTimeUnit = expirationTimeUnit;
    this.expirationTimeValue = expirationTimeValue;
    this.atomicSequenceReserveSize = atomicSequenceReserveSize;
    this.offHeapStorageSize = offHeapStorageSize;
    this.hostIPs = hostIPs;
    this.dnsLookupAddress = dnsLookupAddress;
    this.cacheMemoryMode = cacheMemoryMode;
  }

  public List<String> getHostIPs() {
    return hostIPs;
  }

  public String getOffHeapStorageSize() {
    return offHeapStorageSize;
  }

  public String getMulticastIP() {
    return multicastIP;
  }

  public Integer getMulticastPort() {
    return multicastPort;
  }

  public CacheMode getCacheMode() {
    return cacheMode;
  }

  public Integer getCacheBackupCount() {
    return cacheBackupCount;
  }

  public TimeUnit getExpirationTimeUnit() {
    return expirationTimeUnit;
  }

  public Long getExpirationTimeValue() {
    return expirationTimeValue;
  }

  public Integer getAtomicSequenceReserveSize() {
    return atomicSequenceReserveSize;
  }

  public CacheMemoryMode getCacheMemoryMode() {
    return cacheMemoryMode;
  }

  public String getDnsLookupAddress() {
    return dnsLookupAddress;
  }
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
