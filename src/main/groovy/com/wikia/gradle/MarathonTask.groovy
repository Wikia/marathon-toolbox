package com.wikia.gradle

import com.wikia.gradle.helpers.CommandHelper
import com.wikia.gradle.helpers.VolumesHelper
import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskValidationException

class MarathonTask extends DefaultTask {

    String stage = Defs.Stages.DEVELOPER;

    // force and keep consistency
    // i.e. you can only deploy if the current tag deployed is lower than current version
    Boolean dockerTagPerVersion = false

    Boolean useExternalConfig = true
    Boolean allowOverrideOfExternalConfig = true

    Map<String, String> githubEnvDataSource = [repo: "Wikia/indexing-pipeline", path: "python/env_defaults.sh"]

    String marathonURL = "http://mesos-s1:8080"
    String command
    List<String> args

    String dockerImage = "ubuntu:14.04.1"
    String networkType = Defs.NetworkType.HOST

    List<Map<String, String>> volumes

    Float cpus = 1.5
    Float mem = 300

    Map<String, String> envs = new LinkedHashMap<String, String>()
    Integer instances = null
    List<Integer> ports

    def configFetcher = new GitHubFetcher()
    def marathon = new MarathonConnector(logger)

    def buildRequestJson() {
        def json = new JsonBuilder()
        def root = json {
            id(marathon_id())
            container({
                type Defs.ContainerType.DOCKER
                docker(
                        image: this.dockerImage,
                        network: this.networkType,
                        )
                volumes(VolumesHelper.build(this))
            })
        }
        if (!this.envs.isEmpty()) {
            root.env = this.envs
        }
        root.cpus = this.cpus
        root.mem = this.mem
        if (this.instances != null) {
            root.instances = this.instances
        }
        if (this.ports != null && !this.ports.isEmpty()) {
            root.ports = this.ports
        }

        CommandHelper.build(this, root)
        return json
    }

    def marathon_id() {
        return "/" + [stage, project.group, project.name].join("/")
    }

    def processExternalConfig() {
        if (this.useExternalConfig && this.githubEnvDataSource != null) {
            def cfg = this.configFetcher.fetchWikiaConfig(this.githubEnvDataSource)
            for (tuple in cfg) {
                def key = tuple[0]
                def val = tuple[1]
                if (!(key && val)) {
                    throw new TaskValidationException("external provided invalid value",
                                                      [new InvalidUserDataException(
                                                              key), new InvalidUserDataException(
                                                              value)])
                }
                if (this.allowOverrideOfExternalConfig || !this.envs.containsKey(key)) {
                    this.envs[key] = val
                }
            }
        }
    }

    MarathonTask() {
        if (project.group == '') {
            throw new TaskValidationException("project.group needs to be set",
                                              [new InvalidUserDataException('project.group')])
        }
    }

    def validateData() {
        CommandHelper.validate(this)
        VolumesHelper.validate(this)
    }

    @TaskAction
    def postApp() {
        this.validateData()
        this.processExternalConfig()
        this.marathon.
                postConfig(this.marathonURL, this.marathon_id(), this.buildRequestJson().toString(),
                           this.dockerTagPerVersion)
    }
}