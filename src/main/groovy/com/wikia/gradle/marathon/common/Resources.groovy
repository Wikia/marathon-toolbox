package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone

@AutoClone
class Resources {

    def cpus
    def mem
    def instances
    List<Integer> ports

    def provideOneRandomPort() {
        ports = new ArrayList<>()
        ports.add(0) // 0 means random port will be assigned by marathon
    }

    def provideTwoRandomPorts() {
        ports = new ArrayList<>()
        ports.add(0)
        ports.add(0)
    }

    def validate() {
        for (item in ["cpus", "mem", "instances", "ports"]) {
            if (this.properties.get(item) == null) {
                throw new RuntimeException("Resources.${item} needs to be set")
            }
        }
    }
}
