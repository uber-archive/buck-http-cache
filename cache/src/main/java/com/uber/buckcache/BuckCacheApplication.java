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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.buckcache.datastore.DataStoreProvider;
import com.uber.buckcache.datastore.DataStoreProviderFactory;
import com.uber.buckcache.health.DataStoreHealthChecker;
import com.uber.buckcache.resources.RootResource;
import com.uber.buckcache.resources.buckcache.BuckCacheResource;
import com.uber.buckcache.resources.buckcache.HealthResource;
import com.uber.buckcache.utils.BytesRateLimiter;
import com.uber.buckcache.utils.StatsDClient;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * A REST service that implements Buck's HTTP Cache API
 * (https://buckbuild.com/concept/http_cache_api.html)
 * backed by Ignite.
 */
public class BuckCacheApplication extends Application<BuckCacheConfiguration> {

  private static final Logger logger = LoggerFactory.getLogger(BuckCacheApplication.class);

  private static final String APPLICATION_NAME = "com.Uber.BuckCache";
  private static final String IGNITE_HEALTH_CHECK_NAME = "healthCheck.Ignite";

  public static void main(String[] args) throws Exception {
    new BuckCacheApplication().run(args);
  }

  @Override
  public String getName() {
    return APPLICATION_NAME;
  }

  @Override
  public void initialize(Bootstrap<BuckCacheConfiguration> bootstrap) {

  }

  @Override
  public void run(BuckCacheConfiguration configuration, Environment environment) throws Exception {
    logger.info("**********************************************");
    logger.info("starting with config : {}", configuration);
    logger.info("**********************************************");

    StatsDClient.init(configuration.getStatsd());

    BytesRateLimiter rateLimiter = new BytesRateLimiter(configuration.getThrottleLimit());
    DataStoreProvider dataStoreProvider = DataStoreProviderFactory.get(configuration.getStoreProviderKlass(),
        configuration.getStoreProviderConfig(), configuration.getMode());
    
    dataStoreProvider.start();
    environment.lifecycle().manage(dataStoreProvider);

    environment.healthChecks().register(IGNITE_HEALTH_CHECK_NAME, new DataStoreHealthChecker(dataStoreProvider));

    if (configuration.getMode() != CacheInstanceMode.DATABASE_ONLY) {

      final BuckCacheResource buckCacheResource = new BuckCacheResource(dataStoreProvider, rateLimiter);
      environment.jersey().register(buckCacheResource);
      environment.jersey().register(new HealthResource());
      environment.jersey().register(BuckCacheResource.CacheResultBodyReader.class);
      environment.jersey().register(BuckCacheResource.CacheResultBodyWriter.class);

      final RootResource rootResource = new RootResource();
      environment.jersey().register(rootResource);
    }
  }
}
