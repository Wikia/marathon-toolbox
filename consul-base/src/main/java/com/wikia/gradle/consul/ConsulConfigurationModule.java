package com.wikia.gradle.consul;

import com.wikia.config.annotation.ApplicationName;
import com.wikia.config.annotation.EnvironmentName;
import com.wikia.config.consul.ConfigFolderLocation;
import com.wikia.config.consul.ConsulKeyValueConfig;
import com.wikia.config.guice.DefaultConfigModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.orbitz.consul.Consul;

import java.util.List;

abstract public class ConsulConfigurationModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new DefaultConfigModule());
  }

  @Provides
  @EnvironmentName
  abstract String provideEnvironmentName();

  @Provides
  @ApplicationName
  abstract public String provideApplicationName();

  abstract public String provideConsulUrl();

  @Provides
  ConsulKeyValueConfig provideConsulKeyValueConfig(Consul consul, List<ConfigFolderLocation> list) {
    return new ConsulKeyValueConfig(consul, list);
  }

  @Provides
  Consul providesConsul() {
    return Consul.builder().withUrl(provideConsulUrl()).build();
  }
}
