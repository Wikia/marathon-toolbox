package com.wikia.gradle.marathon.utils

import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskValidationException
import org.kohsuke.github.GitHub

class GitHubFetcher{
    String repository
    String path

    def provide(){
        GitHub gh = GitHub.connect()
        if ( this.repository && this.path) {
            def repo = gh.getRepository(this.repository)
            return parse_env_defaults(repo.getFileContent(this.path).content)
        } else {
            throw new TaskValidationException("external config location not set", [new InvalidUserDataException(this)])
        }
    }

    def validate(){
        ["repository", "path"].forEach({ item ->
            if (this.properties.get(item) == null){
                throw new RuntimeException("GitHubFetcher.${item} needs to be set")
            }
        })
    }

    static parse_env_defaults(String content) {
        def envs = []
        content.split("\n").grep(~/^.*export.*/).each { line ->
            def tmp = (line =~ /(^.*export.*\$\{)|(}.*$)/).replaceAll("")
            envs.add(tmp.split(':='))
        }
        return envs
    }
}
