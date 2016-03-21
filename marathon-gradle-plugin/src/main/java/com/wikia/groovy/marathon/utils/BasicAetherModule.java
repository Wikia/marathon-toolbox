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
import org.sonatype.aether.RepositoryListener;
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.impl.DependencyCollector;
import org.sonatype.aether.impl.Deployer;
import org.sonatype.aether.impl.Installer;
import org.sonatype.aether.impl.LocalRepositoryProvider;
import org.sonatype.aether.impl.MetadataGeneratorFactory;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.impl.RepositoryEventDispatcher;
import org.sonatype.aether.impl.SyncContextFactory;
import org.sonatype.aether.impl.UpdateCheckManager;
import org.sonatype.aether.impl.VersionRangeResolver;
import org.sonatype.aether.impl.VersionResolver;
import org.sonatype.aether.impl.internal.DefaultDependencyCollector;
import org.sonatype.aether.impl.internal.DefaultDeployer;
import org.sonatype.aether.impl.internal.DefaultFileProcessor;
import org.sonatype.aether.impl.internal.DefaultInstaller;
import org.sonatype.aether.impl.internal.DefaultLocalRepositoryProvider;
import org.sonatype.aether.impl.internal.DefaultRemoteRepositoryManager;
import org.sonatype.aether.impl.internal.DefaultRepositoryEventDispatcher;
import org.sonatype.aether.impl.internal.DefaultSyncContextFactory;
import org.sonatype.aether.impl.internal.DefaultUpdateCheckManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManagerFactory;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.spi.io.FileProcessor;
import org.sonatype.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.sonatype.aether.spi.log.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BasicAetherModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ArtifactDescriptorReader.class).to(DefaultArtifactDescriptorReader.class);
    bind(VersionResolver.class).to(DefaultVersionResolver.class);
    bind(VersionRangeResolver.class).to(DefaultVersionRangeResolver.class);
    bind(ModelBuilder.class).to(DefaultModelBuilder.class);
    bind(FileProcessor.class).to(DefaultFileProcessor.class);
    bind(SyncContextFactory.class).to(DefaultSyncContextFactory.class);
    bind(LocalRepositoryManagerFactory.class).to(SimpleLocalRepositoryManagerFactory.class);

    try {
      exceptionalConfigure();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private void exceptionalConfigure() throws NoSuchMethodException {
    bind(RepositoryEventDispatcher.class)
        .toConstructor(DefaultRepositoryEventDispatcher.class.getConstructor(
            Logger.class,
            List.class));
    bind(UpdateCheckManager.class)
        .toConstructor(DefaultUpdateCheckManager.class.getConstructor(
            Logger.class));
    bind(RemoteRepositoryManager.class)
        .toConstructor(DefaultRemoteRepositoryManager.class.getConstructor(
            Logger.class,
            UpdateCheckManager.class,
            List.class));
    bind(DependencyCollector.class)
        .toConstructor(DefaultDependencyCollector.class.getConstructor(
            Logger.class,
            RemoteRepositoryManager.class,
            ArtifactDescriptorReader.class,
            VersionRangeResolver.class
        ));
    bind(Deployer.class)
        .toConstructor(DefaultDeployer.class.getConstructor(
            Logger.class,
            FileProcessor.class,
            RepositoryEventDispatcher.class,
            RemoteRepositoryManager.class,
            UpdateCheckManager.class,
            List.class,
            SyncContextFactory.class
        ));
    bind(Installer.class)
        .toConstructor(DefaultInstaller.class.getConstructor(
            Logger.class,
            FileProcessor.class,
            RepositoryEventDispatcher.class,
            List.class,
            SyncContextFactory.class
        ));
    bind(RepositoryConnectorFactory.class)
        .toConstructor(AsyncRepositoryConnectorFactory.class.getConstructor(
            Logger.class,
            FileProcessor.class
        ));
  }

  @Provides
  List<RepositoryListener> provideRepositoryListeners() {
    return Collections.<RepositoryListener>emptyList();
  }

  @Provides
  LocalRepositoryProvider providesLocalRepositoryProvider(Logger logger,
                                                          LocalRepositoryManagerFactory factory) {
    DefaultLocalRepositoryProvider repositoryProvider = new DefaultLocalRepositoryProvider();
    repositoryProvider.setLogger(logger);
    repositoryProvider.addLocalRepositoryManagerFactory(factory);
    return repositoryProvider;
  }

  @Provides
  List<MetadataGeneratorFactory> provideMetadataGeneratorFactories(
      SnapshotMetadataGeneratorFactory snapshot, VersionsMetadataGeneratorFactory versions) {
    return Arrays.asList(snapshot, versions);
  }

  @Provides
  List<RepositoryConnectorFactory> provideRepositoryConnectorFactories(
      RepositoryConnectorFactory factory) {
    return Collections.singletonList(factory);
  }

}
