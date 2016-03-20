package com.wikia.groovy.marathon.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.SnapshotMetadataGeneratorFactory;
import org.apache.maven.repository.internal.VersionsMetadataGeneratorFactory;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.DependencyCollector;
import org.sonatype.aether.impl.Deployer;
import org.sonatype.aether.impl.Installer;
import org.sonatype.aether.impl.LocalRepositoryProvider;
import org.sonatype.aether.impl.MetadataGeneratorFactory;
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
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.spi.io.FileProcessor;
import org.sonatype.aether.spi.log.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MarathonAetherModule extends AbstractModule {

  @Override
  protected void configure() {
//    install(new AetherModule());
    install(new AetherModule());

    bind(ArtifactDescriptorReader.class)
        .to(DefaultArtifactDescriptorReader.class);
    bind(VersionResolver.class)
        .to(DefaultVersionResolver.class);
    bind(VersionRangeResolver.class)
        .to(DefaultVersionRangeResolver.class);
    bind(ModelBuilder.class)
        .toInstance(new DefaultModelBuilder());
    bind(FileProcessor.class).to(DefaultFileProcessor.class);
  }

  @Provides
  Logger providesLogger() {
    return new Slf4jLogger(LoggerFactory.getLogger("maven"));
  }

  @Provides
  List<MetadataGeneratorFactory> provideMetadataGeneratorFactories(
      SnapshotMetadataGeneratorFactory snapshot, VersionsMetadataGeneratorFactory versions) {
    return Arrays.asList(snapshot, versions);
  }

  @Provides
  RepositorySystem providesRepositorySystem(Logger logger, VersionResolver versionResolver,
                                            VersionRangeResolver versionRangeResolver,
                                            ArtifactResolver artifactResolver,
                                            MetadataResolver metadataResolver,
                                            ArtifactDescriptorReader artifactDescriptorReader,
                                            DependencyCollector dependencyCollector,
                                            Installer installer, Deployer deployer,
                                            LocalRepositoryProvider localRepositoryProvider,
                                            SyncContextFactory syncContextFactory) {
    return new DefaultRepositorySystem(logger, versionResolver, versionRangeResolver,
                                       artifactResolver, metadataResolver, artifactDescriptorReader,
                                       dependencyCollector, installer, deployer,
                                       localRepositoryProvider, syncContextFactory);
  }

  @Provides
  ArtifactResolver providesArtifactResolver(Logger logger, FileProcessor fileProcessor,
                                            RepositoryEventDispatcher repositoryEventDispatcher,
                                            VersionResolver versionResolver,
                                            UpdateCheckManager updateCheckManager,
                                            RemoteRepositoryManager remoteRepositoryManager,
                                            SyncContextFactory syncContextFactory) {
    return new DefaultArtifactResolver(
        logger, fileProcessor, repositoryEventDispatcher, versionResolver, updateCheckManager,
        remoteRepositoryManager, syncContextFactory);
  }

  @Provides
  MetadataResolver providesMetadataResolver(Logger logger,
                                            RepositoryEventDispatcher repositoryEventDispatcher,
                                            UpdateCheckManager updateCheckManager,
                                            RemoteRepositoryManager remoteRepositoryManager,
                                            SyncContextFactory syncContextFactory) {
    return new DefaultMetadataResolver(logger, repositoryEventDispatcher, updateCheckManager,
                                       remoteRepositoryManager, syncContextFactory);
  }

  @Provides
  List<RepositoryConnectorFactory> provideRepositoryConnectorFactories(
      RepositoryConnectorFactory factory) {
    return Collections.singletonList(factory);
  }

  @Provides
  RepositoryConnectorFactory provideRepositoryConnectorFactory(FileProcessor fileProcessor,
                                                               Logger logger) {
    return new AsyncRepositoryConnectorFactory(logger, fileProcessor);
  }
}
