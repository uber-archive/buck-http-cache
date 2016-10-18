package com.uber.buckcache.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.RateLimiter;

public class BytesRateLimiter {
  private static final Logger logger = LoggerFactory.getLogger(BytesRateLimiter.class);

  public enum BIT_UNIT {
    g(1024L * 1024L * 1024L),
    m(1024L * 1024L),
    k(1024L),
    b(1L);

    private final long sizeInBytes;

    BIT_UNIT(long sizeInBytes) {
      this.sizeInBytes = sizeInBytes;
    }

    public long getNumberOfBytes() {
      return sizeInBytes;
    }
  }

  private final Double bytesLimit;
  private final RateLimiter rateLimiter;

  public BytesRateLimiter(String throttleLimit) {
    String multiplier = throttleLimit.substring(0, throttleLimit.length() - 1);
    String unit = throttleLimit.substring(throttleLimit.length() - 1, throttleLimit.length());
    bytesLimit = Double.parseDouble(multiplier) * BIT_UNIT.valueOf(StringUtils.lowerCase(unit)).getNumberOfBytes();
    logger.info("multiplier : {}, unit : {}, bytesLimit : {}", multiplier, unit, bytesLimit);
    rateLimiter = RateLimiter.create(bytesLimit);
  }

  /**
   * This makes sure that both upload and
   * download rates are capped at an instance level.
   * calls are blocked if we try to acquire more bytes than capped size.
   * @param bytes
   */
  public void checkout(int bytes) {
    rateLimiter.acquire(bytes);
  }

}
