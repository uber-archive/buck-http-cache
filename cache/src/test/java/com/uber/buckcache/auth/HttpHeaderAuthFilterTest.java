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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpHeaderAuthFilterTest {
  private static ContainerRequestContext mockRequestContextWithHeader;
  private static ContainerRequestContext mockRequestContextWithoutHeader;

  @BeforeClass
  public static void setup() throws Exception {
    mockRequestContextWithHeader = mock(ContainerRequestContext.class);
    MultivaluedMap mockHeaders = new MultivaluedHashMap<String, String>();
    mockHeaders.add(HttpHeaders.AUTHORIZATION, "testToken");
    when(mockRequestContextWithHeader.getHeaders()).thenReturn(mockHeaders);

    mockRequestContextWithoutHeader = mock(ContainerRequestContext.class);
    when(mockRequestContextWithoutHeader.getHeaders()).thenReturn(new MultivaluedHashMap<>());
  }

  @Test
  public void testSuccessWithEmptyAuthenticatedToken() throws IOException {
    (new HttpHeaderAuthFilter()).filter(mockRequestContextWithHeader);
  }

  @Test
  public void testSuccessWithNullAuthenticatedToken() throws IOException {
    (new HttpHeaderAuthFilter(null)).filter(mockRequestContextWithHeader);
  }

  @Test
  public void testSuccessWithAuthenticatedToken() throws IOException {
    (new HttpHeaderAuthFilter(Arrays.asList("testToken"))).filter(mockRequestContextWithHeader);
  }

  @Test(expected=NotAuthorizedException.class)
  public void testFailWithIncorrectAuthenticatedToken() throws IOException {
    (new HttpHeaderAuthFilter(Arrays.asList("testTokenFail"))).filter(mockRequestContextWithHeader);
  }

  @Test(expected=NotAuthorizedException.class)
  public void testFailWithoutAuthenticatedToken() throws IOException {
    (new HttpHeaderAuthFilter(Arrays.asList("testToken"))).filter(mockRequestContextWithoutHeader);
  }
}
