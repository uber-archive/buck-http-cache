package com.uber.buckcache.integration;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.uber.buckcache.BuckCacheApplication;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleServerIntegrationTest {
  private static final Logger logger = LoggerFactory.getLogger(SingleServerIntegrationTest.class);

  private static CloseableHttpClient httpclient;

  @BeforeClass
  public static void setup() throws Exception {
    httpclient = HttpClients.createDefault();
    File configFile =
        new File(SingleServerIntegrationTest.class.getClassLoader().getResource("config/test-server-1.yml").getFile());
    BuckCacheApplication.main(new String[] {
        "server", configFile.getAbsolutePath()
    });
  }

  @Test
  public void testCacheupload() throws ClientProtocolException, IOException {
    File dataDir = new File(this.getClass().getClassLoader().getResource("cache_data").getFile());
    for (File dataFile : dataDir.listFiles()) {
      logger.info("running testupload on file : {}", dataFile);
      HttpPut httpput = new HttpPut("http://localhost:8090/artifacts/key");
      HttpEntity e = new FileEntity(dataFile);
      httpput.setEntity(e);
      CloseableHttpResponse putResponse = httpclient.execute(httpput);
      try {
        Assert.assertEquals(202, putResponse.getStatusLine().getStatusCode());
      } finally {
        putResponse.close();
      }
    }
  }

  @Test
  public void testCacheDownload() throws IOException {
    File dataDir = new File(SingleServerIntegrationTest.class.getClassLoader().getResource("cache_data").getFile());
    for (File dataFile : dataDir.listFiles()) {
      logger.info("running testDownload on file : {}", dataFile);
      // lets upload again just to make sure
      HttpPut httpput = new HttpPut("http://localhost:8090/artifacts/key");
      HttpEntity e = new FileEntity(dataFile);
      httpput.setEntity(e);
      CloseableHttpResponse putResponse = httpclient.execute(httpput);
      try {
        Assert.assertEquals(202, putResponse.getStatusLine().getStatusCode());
      } finally {
        putResponse.close();
      }

      String[] keys = TestUtils.getCacheKeysForDataFile(dataFile);
      // now lets make sure that each key is found
      logger.info("get query with keys : {}", Arrays.toString(keys));
      for (String key : keys) {
        HttpGet httpget = new HttpGet(String.format("http://localhost:8090/artifacts/key/%s", key));
        CloseableHttpResponse getResponse = httpclient.execute(httpget);
        try {
          Assert.assertEquals(200, getResponse.getStatusLine().getStatusCode());
        } finally {
          getResponse.close();
        }
      }
    }
  }

  @Test
  public void testDataNotFound() throws ClientProtocolException, IOException {
    TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();
    for (int i = 0; i < 10; i++) {
      HttpGet httpget =
          new HttpGet(String.format("http://localhost:8090/artifacts/key/%s", uuidGenerator.generate().toString()));
      CloseableHttpResponse getResponse = httpclient.execute(httpget);
      try {
        Assert.assertEquals(404, getResponse.getStatusLine().getStatusCode());
      } finally {
        getResponse.close();
      }
    }
  }

  @Test
  public void testBadDataUpload() throws ClientProtocolException, IOException {
    TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();
    for (int i = 0; i < 10; i++) {
      HttpPut httpput = new HttpPut("http://localhost:8090/artifacts/key");
      HttpEntity e = new StringEntity(String.format("1%sBadData", uuidGenerator.generate().toString()));
      httpput.setEntity(e);
      CloseableHttpResponse putResponse = httpclient.execute(httpput);
      try {
        Assert.assertEquals(true, putResponse.getStatusLine().getStatusCode() > 400);
      } finally {
        putResponse.close();
      }
    }
  }

  @Test
  public void testCacheDataValidity() throws IOException {
    File dataDir = new File(SingleServerIntegrationTest.class.getClassLoader().getResource("cache_data").getFile());
    for (File dataFile : dataDir.listFiles()) {
      logger.info("running testDownload on file : {}", dataFile);
      // lets upload again just to make sure
      HttpPut httpput = new HttpPut("http://localhost:8090/artifacts/key");
      HttpEntity e = new FileEntity(dataFile);
      httpput.setEntity(e);
      CloseableHttpResponse putResponse = httpclient.execute(httpput);
      try {
        Assert.assertEquals(202, putResponse.getStatusLine().getStatusCode());
      } finally {
        putResponse.close();
      }
      
      String[] keys = TestUtils.getCacheKeysForDataFile(dataFile);
      String key = keys[new Random().nextInt(keys.length)];
      
      // now lets make sure that each key is found
      logger.info("get query with keys : {}", Arrays.toString(keys));
      HttpGet httpget = new HttpGet(String.format("http://localhost:8090/artifacts/key/%s", key));
      CloseableHttpResponse getResponse = httpclient.execute(httpget);
      try {
        Assert.assertEquals(200, getResponse.getStatusLine().getStatusCode());
        String incomingDataFileMD5 = TestUtils.getMD5AfterStrippingKeys(dataFile);
        String  outgoingDataFileMD5 = TestUtils.getMDFForEntireStream(getResponse.getEntity().getContent());
        Assert.assertEquals(incomingDataFileMD5, outgoingDataFileMD5);
      } finally {
        getResponse.close();
      }
    }
  }

  @Test
  public void testCacheuploadWithCustomExpiry() throws Exception {
    File dataDir = new File(this.getClass().getClassLoader().getResource("cache_data").getFile());
    File dataFile = dataDir.listFiles()[0];
    logger.info("running testupload on file : {}", dataFile);
    HttpPut httpput = new HttpPut("http://localhost:8090/artifacts/key");
    HttpEntity e = new FileEntity(dataFile);
    httpput.setEntity(e);
    httpput.addHeader(new BasicHeader("X-Cache-Expiry-Seconds", "0")); // Expire keys immediately
    CloseableHttpResponse putResponse = httpclient.execute(httpput);
    try {
      Assert.assertEquals(202, putResponse.getStatusLine().getStatusCode());
    } finally {
      putResponse.close();
    }

    String[] keys = TestUtils.getCacheKeysForDataFile(dataFile);
    String key = keys[new Random().nextInt(keys.length)];

    // make sure key does not exist after expiration time
    logger.info("get query with keys : {}", Arrays.toString(keys));
    HttpGet httpget = new HttpGet(String.format("http://localhost:8090/artifacts/key/%s", key));
    CloseableHttpResponse getResponse = httpclient.execute(httpget);
    try {
      Assert.assertEquals(404, getResponse.getStatusLine().getStatusCode());
    } finally {
      getResponse.close();
    }
  }
}
