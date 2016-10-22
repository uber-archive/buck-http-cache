package com.uber.buckcache;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class StatsdConfig {
  private boolean enabled;
  private String prefix;
  private String host;
  private int port;
  private double sampleRate;

  public double getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(double sampleRate) {
    this.sampleRate = sampleRate;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
