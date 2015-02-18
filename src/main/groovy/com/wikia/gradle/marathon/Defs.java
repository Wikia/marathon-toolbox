package com.wikia.gradle.marathon;

/**
 * Created by pawelch on 23.01.15.
 */
public class Defs {

  public enum NetworkType {
    HOST, BRIDGE
  }

  public enum ContainerType {
    DOCKER
  }

  public enum VolumeMode {
    RW, RO
  }

  public enum Stages {
    STAGING("stage"), PRODUCTION("prod"), DEVELOPER("dev");
    String stage;
    Stages(String s){
      this.stage = s;
    }

    public String toString() {
      return this.stage;
    }
  }
}
