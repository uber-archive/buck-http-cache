package com.uber.buckcache.utils;

public class MetricsRegistry {
  public static final double SAMPLE_RATE = 1;
  
  public static final String SUMMARY_CALL_COUNT = "summary_api_call_count";

  public static final String CLUSTER_SIZE = "buck_cache_cluster_size";
  
  public static final String GET_CALL_COUNT = "get_api_call_count";
  public static final String GET_CALL_TIME = "get_api_call_time_taken";
  public static final String INCOMING_BYTES_TOTAL_COUNT = "incoming_request_size_in_bytes";
  public static final String INCOMING_BYTES_PER_REQUEST = "incoming_bytes_per_request";
  public static final String IGNITE_CACHE_GET_CALL_COUNT = "ignite_cache_get_count";
  public static final String IGNITE_CACHE_GET_CALL_TIME = "ignite_cache_get_time";
  public static final String CACHE_HIT_COUNT = "cache_hit_count";
  public static final String CACHE_MISS_COUNT  = "cache_miss_count";  
  
  public static final String PUT_CALL_COUNT = "put_api_call_count";
  public static final String PUT_CALL_TIME = "put_api_call_time_taken";
  public static final String OUTGOING_BYTES_TOTAL_COUNT = "outgoing_request_size_in_bytes";
  public static final String OUTGOING_BYTES_PER_REQUEST = "outgoing_bytes_per_request";
  public static final String IGNITE_CACHE_PUT_CALL_COUNT = "ignite_cache_put_count";
  public static final String IGNITE_CACHE_PUT_CALL_TIME = "ignite_cache_put_time";
  
  
  public static final String CPU_COUNT = "buck_cache_server_cpu_usage";
  public static final String CPU_TIME = "buck_cache_server_cpu_usage_timer";
  public static final String HEAP_COUNT = "buck_cache_server_heap_usage";
  public static final String HEAP_TIME = "buck_cache_server_heap_usage_timer";
  public static final String OFF_HEAP_COUNT = "buck_cache_server_off_heap_usage";
  public static final String OFF_HEAP_TIME = "buck_cache_server_off_heap_usage_timer";

  
}
