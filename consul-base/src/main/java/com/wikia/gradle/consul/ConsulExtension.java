package com.wikia.gradle.consul;

import groovy.lang.Closure;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ConsulExtension {

  private Closure<String> url;
  private Closure<String> appName;

  public void setUrl(Closure<String> url) {
    this.url = url;
  }

  public void setUrl(String url) {
    this.url = new Closure<String>(this) {
      @Override
      public String call() {
        return url;
      }
    };
  }

  public void setAppName(Closure<String> appName) {
    this.appName = appName;
  }

  public void setAppName(String appName) {
    this.appName = new Closure<String>(this) {
      @Override
      public String call() {
        return appName;
      }
    };
  }
}
