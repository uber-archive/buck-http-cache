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

import java.util.HashMap;
import java.util.Map;

import org.apache.ignite.events.EventType;

public class IgniteConstants {

  public static final String KEYS_CACHE_NAME = "keys";
  public static final String KEYS_REVERSE_CACHE_NAME = "keys-reverse";
  public static final String METADATA_CACHE_NAME = "metadata";
  public static final String ARTIFACT_CACHE_NAME = "artifacts";
  public static final String UNDERLYING_KEY_SEQUENCE_NAME = "underlyingArtifactKeys";

  public static final Map<Integer, String> EVENT_TYPE_TO_NAME_MAP = new HashMap<Integer, String>() {
    {
      put(EventType.EVT_CACHE_OBJECT_EXPIRED, "EXPIRED");
      put(EventType.EVT_CACHE_OBJECT_REMOVED, "REMOVED");
      put(EventType.EVT_CACHE_ENTRY_EVICTED, "EVICTED");
    }
  };
}
