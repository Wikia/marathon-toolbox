package com.wikia.gradle

import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskValidationException
import org.kohsuke.github.GitHub

class GitHubFetcher {
    def fetchWikiaConfig(fileLoc) {
        // ideally this stuff will be read from Consul or something
        GitHub gh = GitHub.connect()
        if (fileLoc["repo"] && fileLoc["path"]) {
            def repo = gh.getRepository(fileLoc["repo"])
            return parse_env_defaults(repo.getFileContent(fileLoc["path"]).content)
        } else {
            throw new TaskValidationException("external config location not set", [new InvalidUserDataException(externalConfigSourcePerStage[stage])])
        }
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
