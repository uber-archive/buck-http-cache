package com.uber.buckcache.auth;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpHeaderAuthFilterTest {
  private static ContainerRequestContext mockRequestContext;
  private static MultivaluedMap mockHeaders;

  @BeforeClass
  public static void setup() throws Exception {
    mockRequestContext = mock(ContainerRequestContext.class);
    mockHeaders = new MultivaluedHashMap<String, String>();
    mockHeaders.add(HttpHeaders.AUTHORIZATION, "testToken");
    when(mockRequestContext.getHeaders()).thenReturn(mockHeaders);
  }

  @Test
  public void testSuccessWithoutAuthorizedToken() throws IOException {
    (new HttpHeaderAuthFilter()).filter(mockRequestContext);
  }

  @Test
  public void testSuccessWithAuthorizedToken() throws IOException {
    (new HttpHeaderAuthFilter(Arrays.asList("testToken"))).filter(mockRequestContext);
  }

  @Test(expected=NotAuthorizedException.class)
  public void testFailWithAuthorizedToken() throws IOException {
    (new HttpHeaderAuthFilter(Arrays.asList("testTokenFail"))).filter(mockRequestContext);
  }
}
