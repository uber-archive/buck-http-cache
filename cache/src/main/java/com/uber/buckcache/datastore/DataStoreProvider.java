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
package com.uber.buckcache.datastore;

import com.codahale.metrics.health.HealthCheck;
import com.uber.buckcache.CacheInstanceMode;

import io.dropwizard.lifecycle.Managed;

import java.util.concurrent.TimeUnit;

public interface DataStoreProvider extends Managed {

  /**
   * CacheProviderConfig : passes all the configs that are provider in the config.yml file
   * CacheInstanceMode : this tells the cache provider whether to only come up as
   * cacheClient or server or both.
   * @param cacheProviderConfig
   */
  public void init(DataStoreProviderConfig cacheProviderConfig, CacheInstanceMode mode) throws Exception;

  /**
   * returns cache entry associated with the key
   * @param key
   * @return
   */
  public CacheEntry getData(String key) throws Exception;

  /**
   * @param keys : all the keys that associated with this given Cache Entry
   * @param cacheData : data to store/cache.
   */
  public void putData(String[] keys, CacheEntry cacheData) throws Exception;

  /**
   * @param keys : all the keys that associated with this given Cache Entry
   * @param cacheData : data to store/cache
   * @param expirationTimeUnit : the time unit of expiration for the data
   * @param  expirationTimeValue : the time value of expiration for the data
   */
  public void putData(String[] keys, CacheEntry cacheData, TimeUnit expirationTimeUnit, Long expirationTimeValue) throws Exception;

  /**
   * return the current key count of the cache
   * @return
   */
  public int getNumberOfKeys() throws Exception;
  
  /**
   * return the current value count of the cache
   * @return
   */
  public int getNumberOfValues() throws Exception;
  
  /**
   * this is to monitor the health of the data store. 
   * @return
   * @throws Exception
   */
  public HealthCheck.Result check() throws Exception;
  
}
