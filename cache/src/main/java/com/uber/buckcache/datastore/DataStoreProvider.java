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
