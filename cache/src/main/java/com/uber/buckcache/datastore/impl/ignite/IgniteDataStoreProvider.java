package com.uber.buckcache.datastore.impl.ignite;

import static com.uber.buckcache.utils.MetricsRegistry.IGNITE_CACHE_GET_CALL_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.IGNITE_CACHE_GET_CALL_TIME;
import static com.uber.buckcache.utils.MetricsRegistry.IGNITE_CACHE_PUT_CALL_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.IGNITE_CACHE_PUT_CALL_TIME;

import java.io.IOException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import org.slf4j.Logger;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.buckcache.CacheInstanceMode;
import com.uber.buckcache.IgniteConfig;
import com.uber.buckcache.datastore.CacheEntry;
import com.uber.buckcache.datastore.DataStoreProvider;
import com.uber.buckcache.datastore.DataStoreProviderConfig;
import com.uber.buckcache.datastore.exceptions.EntryNotFoundException;
import com.uber.buckcache.utils.StatsDClient;

public class IgniteDataStoreProvider implements DataStoreProvider {
  private static final ObjectMapper om = new ObjectMapper();
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IgniteDataStoreProvider.class);

  private CacheInstanceMode mode;
  private DataStoreProviderConfig cacheProviderConfig;
  private IgniteInstance igniteInstance;
  private IgniteConfig config;

  @Override
  public void init(DataStoreProviderConfig cacheProviderConfig, CacheInstanceMode mode) throws IOException {
    this.cacheProviderConfig = cacheProviderConfig;
    this.mode = mode;
    String configs = om.writeValueAsString(this.cacheProviderConfig.getConfig());
    config = om.readValue(configs, IgniteConfig.class);
    logger.info("initialized IgniteCacheProvider using CacheProviderConfig {} , and Ignite Config {}",
        cacheProviderConfig, config);
    igniteInstance = new IgniteInstance(mode, config);
    igniteInstance.start();
  }

  @Override
  public void start() throws Exception {
    logger.info("ignite cache provider startup sequence complete");
    // nothing to do here
  }

  @Override
  public void stop() throws Exception {
    igniteInstance.stop();
    logger.info("ignite cache provider shutdown sequence complete");
  }

  @Override
  public CacheEntry getData(String key) throws EntryNotFoundException {
    StatsDClient.get().count(IGNITE_CACHE_GET_CALL_COUNT, 1L);
    long start = System.currentTimeMillis();

    Long underlyingId = igniteInstance.getCacheKeys().get(key);

    if (underlyingId != null) {
      byte[] buckCacheData = igniteInstance.getBuckDataCache().get(underlyingId);

      if (buckCacheData != null) {
        StatsDClient.get().recordExecutionTimeToNow(IGNITE_CACHE_GET_CALL_TIME, start);
        return new CacheEntry(buckCacheData);
      }
    }

    throw new EntryNotFoundException("EntryNotFoundException");
  }

  @Override
  public void putData(String[] keys, CacheEntry cacheData) {
    putData(keys, cacheData, Optional.empty());
  }

  @Override
  public void putData(String[] keys, CacheEntry cacheData, TimeUnit expirationTimeUnit, Long expirationTimeValue) throws Exception {
    ExpiryPolicy customExpiryPolicy = new CreatedExpiryPolicy(new Duration(expirationTimeUnit, expirationTimeValue));
    putData(keys, cacheData, Optional.of(customExpiryPolicy));
  }

  @Override
  public int getNumberOfKeys() throws Exception {
    return igniteInstance.getCacheKeys().metrics().getKeySize();
  }

  @Override
  public int getNumberOfValues() throws Exception {
    return igniteInstance.getBuckDataCache().metrics().getKeySize();
  }

  @Override
  public Result check() throws Exception {
    if (igniteInstance == null) {
      return HealthCheck.Result.unhealthy("Ignite instance is null");
    }

    return HealthCheck.Result.healthy("OK");
  }

  private void putData(String[] keys, CacheEntry cacheEntry, Optional<ExpiryPolicy> policy) {
    StatsDClient.get().count(IGNITE_CACHE_PUT_CALL_COUNT, 1L);
    long start = System.currentTimeMillis();
    Long underlyingId = igniteInstance.getAtomicSequence().incrementAndGet();

    // Write backwards, adding the entry first, before adding the keys.
    igniteInstance.getBuckDataCache(policy).put(underlyingId, cacheEntry.getBuckData());
    igniteInstance.getReverseCacheKeys(policy).put(underlyingId, keys);

    for (String key : keys) {
      igniteInstance.getCacheKeys(policy).put(key, underlyingId);
    }
    StatsDClient.get().recordExecutionTimeToNow(IGNITE_CACHE_PUT_CALL_TIME, start);
  }
}
