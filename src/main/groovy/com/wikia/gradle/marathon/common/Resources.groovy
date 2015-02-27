package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone

@AutoClone
class Resources {

    def cpus
    def mem
    def instances
    List<Integer> ports

    def useRandomPorts(Integer num) {
        ports = new ArrayList<>()
        for (int i = 0; i < num; i++) {
            ports.add(0) // 0 means random port will be assigned by marathon
        }
    }

    def validate() {
        for (item in ["cpus", "mem", "instances", "ports"]) {
            if (this.properties.get(item) == null) {
                throw new RuntimeException("Resources.${item} needs to be set")
            }
        }
    }
}
