package com.wikia.gradle.consul;

import com.wikia.config.consul.ConsulKeyValueConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ConsulKVConfigFactory {

  private Injector injector;

  public ConsulKVConfigFactory(ConsulExtension consulExtension, String environmentName) {
    injector = Guice.createInjector(new ConsulConfigurationModule() {
      @Override
      String provideEnvironmentName() {
        return environmentName;
      }

      @Override
      public String provideApplicationName() {
        return consulExtension.getAppName().call();
      }

      @Override
      public String provideConsulUrl() {
        return consulExtension.getUrl().call();
      }
    });
  }

  public ConsulKeyValueConfig provide() {
    return injector.getInstance(ConsulKeyValueConfig.class);
  }
}
