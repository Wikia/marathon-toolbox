package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone

@AutoClone
class MarathonAddress implements Validating {

    String url

    def validate() {
        if (this.properties.get("url") == null) {
            throw new RuntimeException("Marathon.url needs to be set")
        }
    }
}
