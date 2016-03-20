package com.wikia.groovy.marathon.utils;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.io.DefaultMetadataReader;
import org.apache.maven.artifact.repository.metadata.io.MetadataReader;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.MetadataRequest;
import org.sonatype.aether.resolution.MetadataResult;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.metadata.DefaultMetadata;

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
        system.newLocalRepositoryManager(new LocalRepository(Files.createTempDir())));

    this.remoteRepository = new RemoteRepository(REPOSITORY_ID, CONTEXT, repositoryUrl);
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
    DefaultMetadata metadata =
        new DefaultMetadata(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                            MAVEN_METADATA_XML,
                            org.sonatype.aether.metadata.Metadata.Nature.SNAPSHOT);

    MetadataRequest metadataRequest = new MetadataRequest(metadata);
    metadataRequest.setFavorLocalRepository(false);
    session.setUpdatePolicy(UPDATE_POLICY);
    metadataRequest.setRepository(this.remoteRepository);

    List<MetadataResult> res =
        this.system.resolveMetadata(this.session, Arrays.asList(metadataRequest));

    if (res.size() == 0) {
      throw new RuntimeException("Failed fetching artifact metadata from repository");
    }
    Exception ex = res.get(0).getException();
    if (ex != null) {
      throw new RuntimeException(ex);
    }

    Map<String, ?> options = Collections.singletonMap(MetadataReader.IS_STRICT, Boolean.FALSE);

    Metadata mavenMetadata;
    try {
      mavenMetadata = new DefaultMetadataReader().
          read(res.get(0).getMetadata().getFile(), options);
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
