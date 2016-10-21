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
package com.uber.buckcache.utils;

public class MetricsRegistry {
  public static final double SAMPLE_RATE = 1;
  
  public static final String SUMMARY_CALL_COUNT = "summary_api_call_count";
  
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
