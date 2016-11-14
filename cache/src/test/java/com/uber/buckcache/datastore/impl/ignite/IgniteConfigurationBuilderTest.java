package com.uber.buckcache.datastore.impl.ignite;

import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.LookupResult;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IgniteConfigurationBuilderTest {

  private static IgniteConfigurationBuilder builder;

  @Test
  public void testAddMulticastBasedDiscroveryWithoutDNSLookup() throws Exception {
    IgniteConfiguration configuration = builder.addMulticastBasedDiscrovery(
        "",
        0,
        Arrays.asList("127.0.0.2"),
        null).build();
    TcpDiscoverySpi spi = (TcpDiscoverySpi) configuration.getDiscoverySpi();
    Collection<InetSocketAddress> addrs = spi.getIpFinder().getRegisteredAddresses();
    Assert.assertEquals(1, addrs.size());
    Assert.assertEquals("/127.0.0.2:0", addrs.toArray()[0].toString());
  }

  @Test
  public void testAddMulticastBasedDiscroveryWithDNSLookup() throws Exception {
    List<LookupResult> mockAddress = new ArrayList<LookupResult>();
    mockAddress.add(LookupResult.create("127.0.0.3", 1001, 0, 0, 15));
    mockAddress.add(LookupResult.create("127.0.0.4", 1001, 0, 0, 15));
    DnsSrvResolver resolver = mock(DnsSrvResolver.class);
    when(resolver.resolve("testDNSAddress")).thenReturn(mockAddress);
    builder = new IgniteConfigurationBuilder(resolver);

    IgniteConfiguration configuration = builder.addMulticastBasedDiscrovery(
        "",
        0,
        new ArrayList<String>(Arrays.asList("127.0.0.2")),
        "testDNSAddress").build();

    TcpDiscoverySpi spi = (TcpDiscoverySpi) configuration.getDiscoverySpi();
    Collection<InetSocketAddress> addrs = spi.getIpFinder().getRegisteredAddresses();
    Assert.assertEquals(3, addrs.size());
  }
}
