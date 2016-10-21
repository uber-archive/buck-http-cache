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

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;

import org.apache.commons.lang3.StringUtils;
import org.apache.ignite.cache.CacheMemoryMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.AtomicConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import com.google.common.collect.Lists;
import com.uber.buckcache.IgniteConfig;
import com.uber.buckcache.utils.BytesRateLimiter.BIT_UNIT;

public class IgniteConfigurationBuilder {

  private IgniteConfiguration igniteConfiguration;

  public IgniteConfigurationBuilder() {
    igniteConfiguration = new IgniteConfiguration();
  }

  public IgniteConfigurationBuilder addMulticastBasedDiscrovery(String multicastIP, Integer multicastPort, List<String> hostIPs) {
    TcpDiscoverySpi spi = new TcpDiscoverySpi();
    TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
    ((TcpDiscoveryMulticastIpFinder) ipFinder).setMulticastGroup(multicastIP);
    ((TcpDiscoveryMulticastIpFinder) ipFinder).setMulticastPort(multicastPort);
    ((TcpDiscoveryMulticastIpFinder) ipFinder).setAddresses(hostIPs);
    spi.setIpFinder(ipFinder);

    // Override default discovery SPI.
    igniteConfiguration.setDiscoverySpi(spi);
    return this;
  }

  public IgniteConfigurationBuilder addCacheConfiguration(CacheMode cacheMode, Integer backupCount,
      TimeUnit expirationTimeUnit, Long expirationTimeValue, String offHeapMaxSize, String ...caches) {
    
    CacheConfiguration[] cacheConfigs = new CacheConfiguration[caches.length];
    
    for (int i = 0; i < caches.length; i++) {
      CacheConfiguration cacheConfiguration = new CacheConfiguration(caches[i]);
      cacheConfiguration.setCacheMode(cacheMode);
      cacheConfiguration.setMemoryMode(CacheMemoryMode.OFFHEAP_VALUES);
      cacheConfiguration.setStatisticsEnabled(true);

      String multiplier = offHeapMaxSize.substring(0, offHeapMaxSize.length() - 1);
      String unit = offHeapMaxSize.substring(offHeapMaxSize.length() - 1, offHeapMaxSize.length());

      long offHeapMemoryMax =
          Long.parseLong(multiplier) * BIT_UNIT.valueOf(StringUtils.lowerCase(unit)).getNumberOfBytes();
      cacheConfiguration.setOffHeapMaxMemory(offHeapMemoryMax);

      cacheConfiguration.setBackups(backupCount);
      cacheConfiguration
          .setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(expirationTimeUnit, expirationTimeValue)));
      cacheConfigs[i] = cacheConfiguration;
    }
    
    igniteConfiguration.setCacheConfiguration(cacheConfigs);
    return this;
  }

  public IgniteConfigurationBuilder addAtomicSequenceConfig(Integer reserveSize) {
    AtomicConfiguration atomicCfg = new AtomicConfiguration();
    atomicCfg.setAtomicSequenceReserveSize(reserveSize);
    igniteConfiguration.setAtomicConfiguration(atomicCfg);
    return this;
  }

  public IgniteConfiguration build() {
    return igniteConfiguration;
  }
}
