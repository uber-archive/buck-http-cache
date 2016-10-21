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
package com.uber.buckcache.datastore.impl.ignite;

import static com.uber.buckcache.datastore.impl.ignite.IgniteConstants.KEYS_CACHE_NAME;
import static com.uber.buckcache.datastore.impl.ignite.IgniteConstants.KEYS_REVERSE_CACHE_NAME;
import static com.uber.buckcache.datastore.impl.ignite.IgniteConstants.METADATA_CACHE_NAME;
import static com.uber.buckcache.datastore.impl.ignite.IgniteConstants.UNDERLYING_KEY_SEQUENCE_NAME;
import static com.uber.buckcache.utils.MetricsRegistry.CPU_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.CPU_TIME;
import static com.uber.buckcache.utils.MetricsRegistry.HEAP_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.OFF_HEAP_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.OFF_HEAP_TIME;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.cache.expiry.ExpiryPolicy;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.buckcache.CacheInstanceMode;
import com.uber.buckcache.IgniteConfig;
import com.uber.buckcache.utils.StatsDClient;

public class IgniteInstance {
  private static final Logger logger = LoggerFactory.getLogger(IgniteInstance.class);
  private static final int TEN_SECONDS = 10 * 1000;
  private final IgniteConfig config;
  private final IgniteConfiguration igniteConfiguration;
  private final Ignite ignite;

  private final IgniteAtomicSequence atomicSequence;
  private final IgniteCache<String, Long> cacheKeys;
  private final IgniteCache<Long, String[]> reverseCacheKeys;
  private final IgniteCache<Long, byte[]> buckDataCache;
  private final Timer timer = new Timer("ignite_metrics_reporter");

  public IgniteInstance(CacheInstanceMode mode, IgniteConfig config) {
    this.config = config;
    this.igniteConfiguration = new IgniteConfigurationBuilder()
        .addMulticastBasedDiscrovery(this.config.getMulticastIP(), this.config.getMulticastPort(), this.config.getHostIPs())
        .addCacheConfiguration(this.config.getCacheMode(), this.config.getCacheBackupCount(),
            this.config.getExpirationTimeUnit(), this.config.getExpirationTimeValue(),
            this.config.getOffHeapStorageSize(), KEYS_CACHE_NAME, KEYS_REVERSE_CACHE_NAME, METADATA_CACHE_NAME)
        .addAtomicSequenceConfig(config.getAtomicSequencereserveSize()).build();

    logger.info("isClientMode : {}", mode == CacheInstanceMode.CLIENT);
    Ignition.setClientMode(mode == CacheInstanceMode.CLIENT);
    ignite = Ignition.start(igniteConfiguration);
    
    cacheKeys = ignite.cluster().ignite().getOrCreateCache(KEYS_CACHE_NAME);
    reverseCacheKeys = ignite.cluster().ignite().getOrCreateCache(KEYS_REVERSE_CACHE_NAME);
    buckDataCache = ignite.cluster().ignite().getOrCreateCache(METADATA_CACHE_NAME);
    atomicSequence = ignite.cluster().ignite().atomicSequence(UNDERLYING_KEY_SEQUENCE_NAME, 0, true);

    ignite.events().localListen(new LocalCacheEventListener(buckDataCache, reverseCacheKeys, cacheKeys),
        EventType.EVT_CACHE_OBJECT_EXPIRED, EventType.EVT_CACHE_OBJECT_REMOVED);
  }

  public void start() {
    timer.scheduleAtFixedRate(new TimerTask() {

      @Override
      public void run() {
        reportMetrics();
      }
    }, TEN_SECONDS, TEN_SECONDS);
  }

  public void stop() {
    timer.cancel();
    Ignition.stop(ignite.name(), false);
  }

  public IgniteCache<String, Long> getCacheKeys() {
    return cacheKeys;
  }

  public IgniteCache<Long, String[]> getReverseCacheKeys() {
    return reverseCacheKeys;
  }

  public IgniteCache<Long, byte[]> getBuckDataCache() {
    return buckDataCache;
  }

  public IgniteCache<String, Long> getCacheKeys(Optional<ExpiryPolicy> policy) {
    if (policy.isPresent()) {
      return cacheKeys.withExpiryPolicy(policy.get());
    } else {
      return cacheKeys;
    }
  }

  public IgniteCache<Long, String[]> getReverseCacheKeys(Optional<ExpiryPolicy> policy) {
    if (policy.isPresent()) {
      return reverseCacheKeys.withExpiryPolicy(policy.get());
    } else {
      return reverseCacheKeys;
    }
  }

  public IgniteCache<Long, byte[]> getBuckDataCache(Optional<ExpiryPolicy> policy) {
    if (policy.isPresent()) {
      return buckDataCache.withExpiryPolicy(policy.get());
    } else {
      return buckDataCache;
    }
  }

  public IgniteAtomicSequence getAtomicSequence() {
    return atomicSequence;
  }

  public Ignite getIgnite() {
    return ignite.cluster().ignite();
  }

  public void reportMetrics() {
    // TODO: check and make sure that these are not costly to compute
    ClusterNode thisNode = ignite.cluster().localNode();
    ClusterMetrics metrics = thisNode.metrics();

    Double cpuLoad = metrics.getCurrentCpuLoad();
    long heapUsage = metrics.getHeapMemoryUsed();
    long offHeapUsage = metrics.getNonHeapMemoryUsed();

    StatsDClient.get().count(CPU_COUNT, cpuLoad.longValue());
    StatsDClient.get().count(CPU_TIME, cpuLoad.longValue());

    StatsDClient.get().count(HEAP_COUNT, heapUsage);
    StatsDClient.get().count(HEAP_COUNT, heapUsage);

    StatsDClient.get().count(OFF_HEAP_COUNT, offHeapUsage);
    StatsDClient.get().count(OFF_HEAP_TIME, offHeapUsage);
  }
}
