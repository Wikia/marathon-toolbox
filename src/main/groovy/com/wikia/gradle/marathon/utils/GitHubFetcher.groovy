package com.wikia.gradle.marathon.utils

import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskValidationException
import org.kohsuke.github.GitHub

class GitHubFetcher{
    String repository
    String path

    def provide(){
        Map<String, String> fileLocation = [
                repo: this.repository,
                path: this.path]
        fetchWikiaConfig(fileLocation)
    }
    //TODO: remove old compat code
    def fetchWikiaConfig(fileLocation) {
        Map<String, String> githubEnvDataSource = [repo: "Wikia/indexing-pipeline", path: "python/env_defaults.sh"]

        // ideally this stuff will be read from Consul or something
        GitHub gh = GitHub.connect()
        if (fileLocation["repo"] && fileLocation["path"]) {
            def repo = gh.getRepository(fileLocation["repo"])
            return parse_env_defaults(repo.getFileContent(fileLocation["path"]).content)
        } else {
            throw new TaskValidationException("external config location not set", [new InvalidUserDataException(externalConfigSourcePerStage[stage])])
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
