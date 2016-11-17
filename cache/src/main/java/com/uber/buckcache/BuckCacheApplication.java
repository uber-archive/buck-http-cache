package com.uber.buckcache;

import com.uber.buckcache.auth.HttpHeaderAuthFilter;
import io.dropwizard.auth.AuthDynamicFeature;
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

      environment.jersey().register(new AuthDynamicFeature(
          new HttpHeaderAuthFilter(
              configuration.getAuthenticationConfig().getAuthenticatedTokens())));
    }
  }
}
