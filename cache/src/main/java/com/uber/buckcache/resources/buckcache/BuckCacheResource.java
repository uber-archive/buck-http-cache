package com.uber.buckcache.resources.buckcache;

import static com.uber.buckcache.utils.MetricsRegistry.GET_CALL_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.GET_CALL_TIME;
import static com.uber.buckcache.utils.MetricsRegistry.INCOMING_BYTES_PER_REQUEST;
import static com.uber.buckcache.utils.MetricsRegistry.INCOMING_BYTES_TOTAL_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.OUTGOING_BYTES_PER_REQUEST;
import static com.uber.buckcache.utils.MetricsRegistry.OUTGOING_BYTES_TOTAL_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.PUT_CALL_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.PUT_CALL_TIME;
import static com.uber.buckcache.utils.MetricsRegistry.SUMMARY_CALL_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.CACHE_HIT_COUNT;
import static com.uber.buckcache.utils.MetricsRegistry.CACHE_MISS_COUNT;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.concurrent.TimeUnit;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import io.dropwizard.auth.Auth;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.uber.buckcache.datastore.CacheEntry;
import com.uber.buckcache.datastore.DataStoreProvider;
import com.uber.buckcache.datastore.exceptions.DatastoreUnavailableException;
import com.uber.buckcache.datastore.exceptions.EntryNotFoundException;
import com.uber.buckcache.utils.BytesRateLimiter;
import com.uber.buckcache.utils.StatsDClient;
/**
 * Buck HTTP Cache API implementation.
 * See: https://buckbuild.com/concept/http_cache_api.html
 */
@Path("/artifacts")
@Consumes(MediaType.APPLICATION_OCTET_STREAM)
public class BuckCacheResource {
  private static final Logger logger = LoggerFactory.getLogger(BuckCacheResource.class);
  private static final String X_CACHE_EXPIRY_SECONDS = "X-Cache-Expiry-Seconds";
  private static final String X_CACHE_TAGS = "X-Cache-Tags";

  private final DataStoreProvider storeProvider;
  private final BytesRateLimiter rateLimiter;

  public BuckCacheResource(DataStoreProvider storeProvider, BytesRateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
    this.storeProvider = storeProvider;
  }

  private static String getCacheKeyWithCustomizedParam(String key, String cacheTags) {
    if (StringUtils.isEmpty(cacheTags)) {
      return key;
    }
    return String.format("%s.%s", key, cacheTags);
  }

  @GET
  @Path("summary")
  @Produces(MediaType.TEXT_PLAIN)
  public String getSummary() throws Exception {
    StatsDClient.get().count(SUMMARY_CALL_COUNT, 1L);
    return String.format("Storing %d artifacts with %d keys.", storeProvider.getNumberOfValues(),
        storeProvider.getNumberOfKeys());
  }

  @GET
  @Path("key/{key}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getCacheArtifact(@PathParam("key") String key,
                                   @HeaderParam(X_CACHE_TAGS) String cacheTags) throws Exception {
    StatsDClient.get().count(getCacheKeyWithCustomizedParam(GET_CALL_COUNT, cacheTags), 1L);
    CacheEntry cacheEntry;
    long start = System.currentTimeMillis();

    try {
      cacheEntry = storeProvider.getData(key);
      // TODO: need to kill the requests rather than queue here
      rateLimiter.checkout(cacheEntry.getBytes());

      StatsDClient.get().count(OUTGOING_BYTES_TOTAL_COUNT, cacheEntry.getBytes());
      StatsDClient.get().recordExecutionTime(OUTGOING_BYTES_PER_REQUEST, cacheEntry.getBytes());
    } catch (DatastoreUnavailableException ex) {
      StatsDClient.get().recordExecutionTimeToNow(GET_CALL_TIME, start);
      return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    } catch (EntryNotFoundException ex) {
      logger.debug("Cache MISS", key);
      StatsDClient.get().count(getCacheKeyWithCustomizedParam(CACHE_MISS_COUNT, cacheTags), 1L);
      StatsDClient.get().recordExecutionTimeToNow(GET_CALL_TIME, start);
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    logger.debug("Cache hit", key);
    StatsDClient.get().count(getCacheKeyWithCustomizedParam(CACHE_HIT_COUNT, cacheTags), 1L);
    StatsDClient.get().recordExecutionTimeToNow(GET_CALL_TIME, start);
    return Response.ok(cacheEntry).build();
  }

  @PUT
  @PermitAll
  @Path("key")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  public Response addArtifactToCache(PutCacheEntry putCacheEntry,
                                     @HeaderParam(X_CACHE_EXPIRY_SECONDS) String cacheExpirySeconds) throws Exception {
    StatsDClient.get().count(PUT_CALL_COUNT, 1L);

    long start = System.currentTimeMillis();
    if (putCacheEntry.verify()) {
      try {
        // TODO: need to kill the requests rather than queue here
        rateLimiter.checkout(putCacheEntry.getBytes());

        if (!StringUtils.isEmpty(cacheExpirySeconds)) {
          try {
            Long expiryTime = NumberUtils.createLong(cacheExpirySeconds);
            storeProvider.putData(putCacheEntry.getKeys(),
                    putCacheEntry.getCacheEntry(),
                    TimeUnit.SECONDS,
                    expiryTime);
          } catch (NumberFormatException e) {
            storeProvider.putData(putCacheEntry.getKeys(), putCacheEntry.getCacheEntry());
          }
        } else {
          storeProvider.putData(putCacheEntry.getKeys(), putCacheEntry.getCacheEntry());
        }

        StatsDClient.get().recordExecutionTimeToNow(PUT_CALL_TIME, start);

        StatsDClient.get().count(INCOMING_BYTES_TOTAL_COUNT, putCacheEntry.getBytes());
        StatsDClient.get().recordExecutionTime(INCOMING_BYTES_PER_REQUEST, putCacheEntry.getBytes());

        return Response.status(Response.Status.ACCEPTED).build();
      } catch (DatastoreUnavailableException ex) {
        StatsDClient.get().recordExecutionTimeToNow(PUT_CALL_TIME, start);
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
      }
    } else {
      StatsDClient.get().recordExecutionTimeToNow(PUT_CALL_TIME, start);
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }
  }

  @Provider
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public static class CacheResultBodyWriter implements MessageBodyWriter<CacheEntry> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == CacheEntry.class;
    }

    @Override
    public long getSize(CacheEntry cacheEntry, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType) {
      return cacheEntry.getBytes();
    }

    @Override
    public void writeTo(CacheEntry cacheEntry, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
      DataOutputStream dataOutputStream = new DataOutputStream(entityStream);
      dataOutputStream.write(cacheEntry.getBuckData());
    }
  }

  @Provider
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  public static class CacheResultBodyReader implements MessageBodyReader<PutCacheEntry> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == PutCacheEntry.class;
    }

    @Override
    public PutCacheEntry readFrom(Class<PutCacheEntry> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
      int sizeInBytes = 0;
      DataInputStream dataInputStream = new DataInputStream(entityStream);

      int nKeys = dataInputStream.readInt();
      sizeInBytes += Integer.BYTES;

      String[] keys = new String[nKeys];
      for (int i = 0; i < nKeys; i++) {
        keys[i] = dataInputStream.readUTF();
        sizeInBytes += keys[i].length();
      }

      byte[] buckData = ByteStreams.toByteArray(dataInputStream);
      sizeInBytes += buckData.length;

      return new PutCacheEntry(keys, buckData, sizeInBytes);
    }
  }
}
