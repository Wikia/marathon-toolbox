package com.wikia.gradle.marathon.common

import com.wikia.gradle.marathon.utils.GitHubFetcher
import groovy.transform.AutoClone

@AutoClone
class MarathonConfig {

    String url
    GitHubFetcher gitHubFetcher

    def readConfigFromGithub(Closure closure) {
        if (gitHubFetcher == null){
            gitHubFetcher = new GitHubFetcher()
        }
        closure.delegate = gitHubFetcher
        closure()
        gitHubFetcher.validate()
    }

    def validate() {
        ["url"].each({ item ->
            if (this.properties.get(item) == null) {
                throw new RuntimeException("Marathon.${item} needs to be set")
            }
        })
    }
}
