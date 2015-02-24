package com.wikia.gradle.marathon.common

import com.wikia.gradle.marathon.utils.GitHubFetcher
import groovy.transform.AutoClone

@AutoClone
class MarathonAddress {

    String url
    GitHubFetcher gitHubFetcher

    def readConfigFromGithub(Closure closure) {
        if (gitHubFetcher == null) {
            gitHubFetcher = new GitHubFetcher()
        }
        closure.delegate = gitHubFetcher
        closure()
        gitHubFetcher.validate()
    }

    def validate() {
        if (this.properties.get("url") == null) {
            throw new RuntimeException("Marathon.url needs to be set")
        }
    }
}
