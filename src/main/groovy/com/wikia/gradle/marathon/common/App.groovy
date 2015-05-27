package com.wikia.gradle.marathon.common

import com.wikia.groovy.marathon.utils.ArtifactLocator
import groovy.transform.AutoClone
import org.gradle.api.Project

@AutoClone
class App implements Validating {

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

    @Deprecated
    def dropwizardApplication(String configName = null) {
        dropwizardCommand("server", configName)
    }

    def dropwizardCommand(String command, String configName = "") {
        executeApplicationZip(command, { Project project ->
            if (configName == null || configName.isEmpty()) {
                configName = "${project.name}.yaml"
            }
            "${subdirInZip(project)}/conf/${configName}"
        })

        this.artifactExtension = "zip"
    }

    def dropwizardCommandClasspathConfig(String command, String configName = "",
                                         String classpathConfigPathPrefix = "") {
        executeApplicationZip(command, { Project project ->
            if (configName == null || configName.isEmpty()) {
                configName = "${project.name}.yaml"
            }
            "classpath:${classpathConfigPathPrefix}${configName}"
        })
    }

    def executeApplicationZip(Object... args) {
        this.executablePath = { Project project ->
            "${subdirInZip(project)}/bin/${project.applicationName}"
        }

        this.arguments = { Project project ->
            args.collect({ arg ->
                if (arg instanceof Closure) {
                    return arg(project).toString()
                } else {
                    return arg.toString()
                }
            })
        }

        this.artifactExtension = "zip"
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
                        "ExecutablePath not set. Did you forget to declare an application?")
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

    private static String subdirInZip(Project project) {
        project.distZip.archiveName - ".${project.distZip.extension}"
    }
}
