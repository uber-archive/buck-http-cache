package com.uber.buckcache.datastore;

import com.uber.buckcache.CacheInstanceMode;

public class DataStoreProviderFactory {

  
  /**
   * returns the correct cache provider impl.
   * @param datastoreType
   * @param providerConfig
   * @param mode
   * @return
   * @throws Exception 
   */
  public static DataStoreProvider get(String klass, DataStoreProviderConfig providerConfig, CacheInstanceMode mode) throws Exception {
    DataStoreProvider cacheProvider = (DataStoreProvider) Class.forName(klass).newInstance();
    cacheProvider.init(providerConfig, mode);
    return cacheProvider;
  }
}
