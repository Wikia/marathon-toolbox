package com.wikia.gradle.marathon.common

import com.wikia.gradle.marathon.utils.ArtifactLocator
import org.gradle.api.Project

class App {

    Closure<String> image = null
    Closure<String> uri = null
    Closure<String> cmd = null
    Closure<List<String>> arguments = null

    String artifactExtension = "jar"
    Closure<String> executablePath = null

    def mavenSource(String repositoryUrl) {
        if (this.image != null) {
            throw new RuntimeException("Docker image specified before binary URI has been provided")
        }
        uri = { Project project ->
            new ArtifactLocator(repositoryUrl).getUrl(
                    project.group.toString(),
                    project.name,
                    this.artifactExtension,
                    project.version.toString()
            )
        }
        cmd = { Project project ->
            "${executablePath(project)} ${arguments(project).join(" ")}"
        }
    }

    def dropwizardApplication(String configName = null) {
        def subdir = { Project project ->
            project.distZip.archiveName - ".${project.distZip.extension}"
        }
        this.executablePath = { Project project ->
            "${subdir(project)}/bin/${project.applicationName}"
        }

        this.arguments = { Project project ->
            if (configName == null) {
                configName = "${project.name}.yaml"
            }
            ["server", "${subdir(project)}/conf/${configName}"]
        }
        artifactExtension = "zip"
    }

    def docker(imageName) {
        image = { imageName }
        if (this.uri != null) {
            throw new RuntimeException("Docker image specified after binary URI has been provided")
        }
    }

    def isDocker() {
        image != null
    }

    def validate() {
        if (this.properties.get("cmd") == null) {
            throw new RuntimeException("App.cmd needs to be set")
        }
    }
}
