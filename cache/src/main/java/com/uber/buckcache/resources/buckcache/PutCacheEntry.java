package com.uber.buckcache.resources.buckcache;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.uber.buckcache.datastore.CacheEntry;

public class PutCacheEntry {
  private final String[] keys;
  private final CacheEntry cacheEntry;
  private final int sizeInBytes;

  public PutCacheEntry(String[] key, byte[] buckData, int sizeInBytes) {
    this.keys = key;
    this.sizeInBytes = sizeInBytes;
    this.cacheEntry = new CacheEntry(buckData);
  }

  public boolean verify() {
    return (keys != null && cacheEntry.getBuckData() != null && keys.length > 0 && cacheEntry.getBuckData().length > 0);
  }

  public String[] getKeys() {
    return keys;
  }

  public int getBytes() {
    return sizeInBytes;
  }

  public CacheEntry getCacheEntry() {
    return cacheEntry;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
