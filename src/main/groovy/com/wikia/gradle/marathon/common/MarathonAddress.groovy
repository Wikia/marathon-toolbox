package com.wikia.gradle.marathon.common

import com.wikia.gradle.marathon.utils.GitHubFetcher
import groovy.transform.AutoClone

@AutoClone
class MarathonAddress {

    String url

    def validate() {
        if (this.properties.get("url") == null) {
            throw new RuntimeException("Marathon.url needs to be set")
        }
    }
}
