package com.wikia.gradle.marathon.common;


import com.wikia.gradle.marathon.utils.ArtifactLocator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArtifactLocatorTest {

  /**
   * this test works only on live maven repository instance
   * */
  @Test
  public void test() {

    ArtifactLocator al =
        new ArtifactLocator("https://oss.sonatype.org/content/groups/google-with-staging");
    String url = al.getUrl("com.google.gwt", "gwt", "2.6-SNAPSHOT");
    assertEquals(url,
                 "https://oss.sonatype.org/content/groups/google-with-staging/com/google/gwt/gwt/2.6-SNAPSHOT/gwt-2.6-20131107.002742-1.jar");
  }
}
