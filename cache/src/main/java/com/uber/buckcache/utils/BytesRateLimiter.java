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
