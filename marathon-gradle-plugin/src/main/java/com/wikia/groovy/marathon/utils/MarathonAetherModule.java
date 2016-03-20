package com.wikia.groovy.marathon.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.DependencyCollector;
import org.sonatype.aether.impl.Deployer;
import org.sonatype.aether.impl.Installer;
import org.sonatype.aether.impl.LocalRepositoryProvider;
import org.sonatype.aether.impl.MetadataResolver;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.impl.RepositoryEventDispatcher;
import org.sonatype.aether.impl.SyncContextFactory;
import org.sonatype.aether.impl.UpdateCheckManager;
import org.sonatype.aether.impl.VersionRangeResolver;
import org.sonatype.aether.impl.VersionResolver;
import org.sonatype.aether.impl.internal.DefaultArtifactResolver;
import org.sonatype.aether.impl.internal.DefaultFileProcessor;
import org.sonatype.aether.impl.internal.DefaultMetadataResolver;
import org.sonatype.aether.impl.internal.DefaultRepositorySystem;
import org.sonatype.aether.impl.internal.Slf4jLogger;
import org.sonatype.aether.spi.io.FileProcessor;
import org.sonatype.aether.spi.log.Logger;

public class MarathonAetherModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new BasicAetherModule());
    try {
      bind(RepositorySystem.class).toConstructor(DefaultRepositorySystem.class.getConstructor(
          Logger.class,
          VersionResolver.class,
          VersionRangeResolver.class,
          ArtifactResolver.class,
          MetadataResolver.class,
          ArtifactDescriptorReader.class,
          DependencyCollector.class,
          Installer.class,
          Deployer.class,
          LocalRepositoryProvider.class,
          SyncContextFactory.class
      ));
      bind(ArtifactResolver.class)
          .toConstructor(DefaultArtifactResolver.class.getConstructor(
              Logger.class,
              FileProcessor.class,
              RepositoryEventDispatcher.class,
              VersionResolver.class,
              UpdateCheckManager.class,
              RemoteRepositoryManager.class,
              SyncContextFactory.class
          ));
      bind(MetadataResolver.class)
          .toConstructor(DefaultMetadataResolver.class.getConstructor(
              Logger.class,
              RepositoryEventDispatcher.class,
              UpdateCheckManager.class,
              RemoteRepositoryManager.class,
              SyncContextFactory.class
          ));
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  Logger providesLogger() {
    return new Slf4jLogger(LoggerFactory.getLogger("maven"));
  }
}
