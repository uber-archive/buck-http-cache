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

import static com.uber.buckcache.datastore.impl.ignite.IgniteConstants.ARTIFACT_CACHE_NAME;
import static com.uber.buckcache.datastore.impl.ignite.IgniteConstants.EVENT_TYPE_TO_NAME_MAP;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.lang.IgnitePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalCacheEventListener implements IgnitePredicate<CacheEvent> {
  private static Logger logger = LoggerFactory.getLogger(LocalCacheEventListener.class);

  private final IgniteCache<Long, byte[]> metadataCache;
  private final IgniteCache<Long, String[]> reverseCacheKeys;
  private final IgniteCache<String, Long> cacheKeys;

  public LocalCacheEventListener(IgniteCache<Long, byte[]> metadataCache, IgniteCache<Long, String[]> reverseCacheKeys,
      IgniteCache<String, Long> cacheKeys) {
    this.metadataCache = metadataCache;
    this.reverseCacheKeys = reverseCacheKeys;
    this.cacheKeys = cacheKeys;
  }

  @Override
  public boolean apply(CacheEvent evt) {
    if (evt.cacheName().equals(ARTIFACT_CACHE_NAME)) {

      final String eventName =
          EVENT_TYPE_TO_NAME_MAP.containsKey(evt.type()) ? EVENT_TYPE_TO_NAME_MAP.get(evt.type()) : "UNKNOWN";
      Long underlyingKey = evt.key();
      String[] keys = reverseCacheKeys.getAndRemove(underlyingKey);

      logger.info(String.format("Artifact cache event {} with key {}.", eventName, underlyingKey), keys, evt);

      for (String key : keys) {
        cacheKeys.remove(key);
      }

      metadataCache.remove(underlyingKey);
    }

    return true;
  }

}
