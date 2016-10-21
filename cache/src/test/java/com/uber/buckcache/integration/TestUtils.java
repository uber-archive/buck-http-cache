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
package com.uber.buckcache.integration;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;

public class TestUtils {

  public static String[] getCacheKeysForDataFile(DataInputStream ds) throws IOException {
    int numKeys = ds.readInt();
    String[] keys = new String[numKeys];
    for (int i = 0; i < keys.length; i++) {
      keys[i] = ds.readUTF();
    }
    ds.close();
    return keys;
  }

  public static String[] getCacheKeysForDataFile(File dataFile) throws IOException {
    return getCacheKeysForDataFile(new DataInputStream(new FileInputStream(dataFile)));
  }

  public static String getMDFForEntireStream(InputStream dis) throws IOException {
    String md5 = DigestUtils.md5Hex(dis);
    dis.close();
    return md5;
  }

  public static String getMD5AfterStrippingKeys(File dataFile) throws IOException {
    DataInputStream dis = new DataInputStream(new FileInputStream(dataFile));

    // lets skip the keys
    int numKeys = dis.readInt();
    String[] keys = new String[numKeys];
    for (int i = 0; i < keys.length; i++) {
      keys[i] = dis.readUTF();
    }

    String md5 = DigestUtils.md5Hex(dis);
    dis.close();
    return md5;
  }

  public static void main(String[] args) throws ClientProtocolException, IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    File dataDir = new File(TestUtils.class.getClassLoader().getResource("cache_data").getFile());
    for (File dataFile : dataDir.listFiles()) {
      HttpPut httpput = new HttpPut("http://localhost:6457/artifacts/key");
      HttpEntity e = new FileEntity(dataFile);
      httpput.setEntity(e);
      CloseableHttpResponse putResponse = httpclient.execute(httpput);
      try {
        Assert.assertEquals(202, putResponse.getStatusLine().getStatusCode());
      } finally {
        putResponse.close();
      }
      
      System.out.println(Arrays.toString(getCacheKeysForDataFile(new DataInputStream(new FileInputStream(dataFile)))));
      break;
    }
  }
}
