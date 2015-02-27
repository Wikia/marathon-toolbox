package com.wikia.groovy.marathon.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.SnapshotMetadataGeneratorFactory;
import org.apache.maven.repository.internal.VersionsMetadataGeneratorFactory;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.impl.guice.AetherModule;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

public class MarathonAetherModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new AetherModule());
    bind(ArtifactDescriptorReader.class)
        .to(DefaultArtifactDescriptorReader.class).in(Singleton.class);
    bind(VersionResolver.class)
        .to(DefaultVersionResolver.class).in(Singleton.class);
    bind(VersionRangeResolver.class)
        .to(DefaultVersionRangeResolver.class).in(Singleton.class);
    bind(MetadataGeneratorFactory.class).annotatedWith(Names.named("snapshot"))
        .to(SnapshotMetadataGeneratorFactory.class).in(Singleton.class);
    bind(MetadataGeneratorFactory.class).annotatedWith(Names.named("versions"))
        .to(VersionsMetadataGeneratorFactory.class).in(Singleton.class);
    bind(ModelBuilder.class)
        .toInstance(new DefaultModelBuilder());
    bind(RepositoryConnectorFactory.class).annotatedWith(Names.named("basic"))
        .to(BasicRepositoryConnectorFactory.class);
    bind(TransporterFactory.class).annotatedWith(Names.named("file"))
        .to(FileTransporterFactory.class);
    bind(TransporterFactory.class).annotatedWith(Names.named("http"))
        .to(HttpTransporterFactory.class);
  }

  @Provides
  @Singleton
  Set<MetadataGeneratorFactory> provideMetadataGeneratorFactories(
      @Named("snapshot") MetadataGeneratorFactory snapshot,
      @Named("versions") MetadataGeneratorFactory versions) {
    Set<MetadataGeneratorFactory> factories = new HashSet<MetadataGeneratorFactory>();
    factories.add(snapshot);
    factories.add(versions);

    return Collections.unmodifiableSet(factories);
  }

  @Provides
  @Singleton
  Set<RepositoryConnectorFactory> provideRepositoryConnectorFactories(
      @Named("basic") RepositoryConnectorFactory basic) {
    Set<RepositoryConnectorFactory> factories = new HashSet<>();
    factories.add(basic);
    return Collections.unmodifiableSet(factories);
  }

  @Provides
  @Singleton
  Set<TransporterFactory> provideTransporterFactories(@Named("file") TransporterFactory file,
                                                      @Named("http") TransporterFactory http) {
    Set<TransporterFactory> factories = new HashSet<>();
    factories.add(file);
    factories.add(http);
    return Collections.unmodifiableSet(factories);
  }
}
