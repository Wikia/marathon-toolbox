package com.wikia.groovy.marathon.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.sonatype.aether.RepositoryListener;
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
import org.sonatype.aether.impl.internal.DefaultDependencyCollector;
import org.sonatype.aether.impl.internal.DefaultDeployer;
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

import java.util.Collections;
import java.util.List;

public class AetherModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(new TypeLiteral<List<RepositoryListener>>() {
    }).toInstance(Collections.<RepositoryListener>emptyList());
    bind(SyncContextFactory.class).to(DefaultSyncContextFactory.class);
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
  LocalRepositoryManagerFactory providesLocalRepositoryManagerFactory() {
    return new SimpleLocalRepositoryManagerFactory();
  }

  @Provides
  RepositoryEventDispatcher providerRepositoryEventDispatcher(
      Logger logger, List<RepositoryListener> listeners) {
    return new DefaultRepositoryEventDispatcher(logger, listeners);
  }

  @Provides
  UpdateCheckManager provideUpdateCheckManager(Logger logger) {
    return new DefaultUpdateCheckManager(logger);
  }

  @Provides
  RemoteRepositoryManager provideRemoteRespositoryManager(
      Logger logger, UpdateCheckManager updateCheckManager,
      List<RepositoryConnectorFactory> connectorFactories) {
    return new DefaultRemoteRepositoryManager(logger, updateCheckManager, connectorFactories);
  }

  @Provides
  DependencyCollector provideSyncContextFactory(Logger logger,
                                                RemoteRepositoryManager remoteRepositoryManager,
                                                ArtifactDescriptorReader artifactDescriptorReader,
                                                VersionRangeResolver versionRangeResolver) {
    return new DefaultDependencyCollector(logger, remoteRepositoryManager,
                                          artifactDescriptorReader, versionRangeResolver);
  }

  @Provides
  Deployer providesDeployer(Logger logger, FileProcessor fileProcessor,
                            RepositoryEventDispatcher repositoryEventDispatcher,
                            RemoteRepositoryManager remoteRepositoryManager,
                            UpdateCheckManager updateCheckManager,
                            List<MetadataGeneratorFactory> metadataFactories,
                            SyncContextFactory syncContextFactory) {
    return new DefaultDeployer(logger, fileProcessor, repositoryEventDispatcher,
                               remoteRepositoryManager, updateCheckManager, metadataFactories,
                               syncContextFactory);
  }

  @Provides
  Installer providesInstaller(Logger logger, FileProcessor fileProcessor,
                              RepositoryEventDispatcher repositoryEventDispatcher,
                              List<MetadataGeneratorFactory> metadataFactories,
                              SyncContextFactory syncContextFactory) {
    return new DefaultInstaller(logger, fileProcessor, repositoryEventDispatcher, metadataFactories,
                                syncContextFactory);
  }

}
