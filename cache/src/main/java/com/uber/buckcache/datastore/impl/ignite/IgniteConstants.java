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
