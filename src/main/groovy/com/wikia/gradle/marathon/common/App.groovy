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

    String mavenPublishTaskName = null

    def mavenSource(String repositoryUrl, String mavenPublishTaskName) {
        this.mavenPublishTaskName = mavenPublishTaskName
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

    def dockerSource(imageName) {
        image = { imageName }
        if (this.uri != null) {
            throw new RuntimeException("Docker image specified after binary URI has been provided")
        }
    }

    def isMaven() {
        this.mavenPublishTaskName != null
    }

    def isDocker() {
        this.image != null
    }

    def validate() {
        if (this.cmd == null) {
            throw new RuntimeException("App.cmd needs to be set")
        }
        if (this.isMaven()) {
            if (this.executablePath == null) {
                throw new RuntimeException(
                        "ExecutablePath not set. Did you forget to declare application?")
            }
        }
        if (this.arguments == null) {
            throw new RuntimeException(
                    "Arguments not set. Did you forget to declare application?")
        }

        if (this.isDocker() && this.isMaven()) {
            throw new RuntimeException(
                    "Both docker and maven application source have been specified.")
        }
    }

    String uriProvider(Project project) {
        this.uri(project)
    }

    String cmdProvider(Project project) {
        this.cmd(project)
    }

    String imageProvider(Project project) {
        this.image(project)
    }
}

