package com.wikia.gradle.marathon.utils;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.io.DefaultMetadataReader;
import org.apache.maven.artifact.repository.metadata.io.MetadataReader;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ArtifactLocator {

  public static final String MAVEN_METADATA_XML = "maven-metadata.xml";
  public static final String UPDATE_POLICY = "always";
  public static final String CONTEXT = "default";
  public static final String REPOSITORY_ID = "repository";
  private RepositorySystem system;
  private DefaultRepositorySystemSession session;
  private RemoteRepository remoteRepository;

  private String repositoryUrl;

  public ArtifactLocator(String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
    this.setupMaven();
  }

  private void setupMaven() {
    Injector injector = Guice.createInjector(new MarathonAetherModule());
    this.system = injector.getInstance(RepositorySystem.class);
    this.session = new DefaultRepositorySystemSession();

    session.setLocalRepositoryManager(
        system.newLocalRepositoryManager(session, new LocalRepository(Files.createTempDir())));

    this.remoteRepository = new RemoteRepository.Builder(REPOSITORY_ID, CONTEXT,
                                                         repositoryUrl)
        .build();

  }

  public String getUrl(String groupId, String artifactId, String extenstion, String version) {
    Artifact artifact = new DefaultArtifact(groupId, artifactId, extenstion, version);

    String path;
    if (artifact.isSnapshot()) {
      path = fetchSnapshotPath(artifact);
    } else {
      path = getArtifactPath(artifact);
    }
    return StringUtils.chomp(this.repositoryUrl, "/") + "/" + path;
  }

  public Metadata fetchMetadata(Artifact artifact) {
    org.eclipse.aether.metadata.Metadata metadata =
        new DefaultMetadata(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                            MAVEN_METADATA_XML,
                            org.eclipse.aether.metadata.Metadata.Nature.SNAPSHOT);

    MetadataRequest metadataRequest = new MetadataRequest(metadata);
    metadataRequest.setFavorLocalRepository(false);
    session.setUpdatePolicy(UPDATE_POLICY);
    metadataRequest.setRepository(this.remoteRepository);

    List<MetadataResult>
        res =
        this.system.resolveMetadata(this.session, Arrays.asList(metadataRequest));
    MetadataResult metadataResult = res.get(0);
    if (metadataResult.getMetadata() == null ){
      throw new RuntimeException("Failed fetching artifact metadata from repository");
    }

    Map<String, ?> options = Collections.singletonMap(MetadataReader.IS_STRICT, Boolean.FALSE);

    Metadata mavenMetadata;
    try {
      mavenMetadata = new DefaultMetadataReader().
          read(metadataResult.getMetadata().getFile(), options);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return mavenMetadata;
  }


  public String fetchSnapshotPath(Artifact artifact) {
    Metadata mavenMetadata = fetchMetadata(artifact);

    Snapshot snapshot = mavenMetadata.getVersioning().getSnapshot();

    // this newVersion schema is taken from Maven code, there is no library call available
    String newVersion = snapshot.getTimestamp() + "-" + snapshot.getBuildNumber();
    return StringUtils.replace(
        this.getArtifactPath(artifact),
        org.apache.maven.artifact.Artifact.SNAPSHOT_VERSION + "." + artifact.getExtension(),
        newVersion + "." + artifact.getExtension()
    );
  }

  public String getArtifactPath(Artifact artifact) {
    String path = this.session.getLocalRepositoryManager()
        .getPathForRemoteArtifact(artifact, this.remoteRepository, CONTEXT);
    if (StringUtils.endsWith(path, "." + artifact.getExtension())) {
      return path;
    } else {
      return path + "." + artifact.getExtension();
    }
  }

}
