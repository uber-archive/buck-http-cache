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
package com.uber.buckcache.resources.buckcache;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.uber.buckcache.datastore.CacheEntry;

public class PutCacheEntry {
  private final String[] keys;
  private final CacheEntry cacheEntry;
  private final int sizeInBytes;

  public PutCacheEntry(String[] key, byte[] buckData, int sizeInBytes) {
    this.keys = key;
    this.sizeInBytes = sizeInBytes;
    this.cacheEntry = new CacheEntry(buckData);
  }

  public boolean verify() {
    return (keys != null && cacheEntry.getBuckData() != null && keys.length > 0 && cacheEntry.getBuckData().length > 0);
  }

  public String[] getKeys() {
    return keys;
  }

  public int getBytes() {
    return sizeInBytes;
  }

  public CacheEntry getCacheEntry() {
    return cacheEntry;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
