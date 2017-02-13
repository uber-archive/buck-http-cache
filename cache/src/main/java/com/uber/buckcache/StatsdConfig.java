package com.uber.buckcache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class StatsdConfig {
  private final boolean enabled;
  private final String prefix;
  private final String host;
  private final int port;
  private final double sampleRate;

  @JsonCreator
  public StatsdConfig(
      @JsonProperty("enabled") boolean enabled,
      @JsonProperty("prefix") String prefix,
      @JsonProperty("host") String host,
      @JsonProperty("port") int port,
      @JsonProperty("sampleRate") double sampleRate) {
    this.enabled = enabled;
    this.prefix = prefix;
    this.host = host;
    this.port = port;
    this.sampleRate = sampleRate;
  }

  public double getSampleRate() {
    return sampleRate;
  }

  public String getPrefix() {
    return prefix;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
