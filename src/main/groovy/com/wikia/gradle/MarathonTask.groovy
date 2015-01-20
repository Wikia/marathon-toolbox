package com.wikia.gradle

import com.wikia.gradle.helpers.CommandHelper
import com.wikia.gradle.helpers.VolumesHelper
import groovy.json.JsonBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskValidationException

class MarathonTask extends DefaultTask {
    public enum NetworkType {
        HOST, BRIDGE
    }

    enum ContainerType {
        DOCKER
    }

    enum VolumeMode {
        RW, RO
    }

    String stage = "test"

    // force and keep consistency
    // i.e. you can only deploy if the current tag deployed is lower than current version
    Boolean dockerTagPerVersion = false

    Boolean useExternalConfig = true
    Boolean allowOverrideOfExternalConfig = true
    def externalConfigSourcePerStage = [stage: [repo: "Wikia/indexing-pipeline", path: "env_defaults.sh"],
                                        prod : [repo: "Wikia/indexing-pipeline", path: "env_defaults.sh"],
                                        test : [repo: "Wikia/indexing-pipeline", path: "env_defaults.sh"]]

    String marathonURL = "http://mesos-s1:8080"
    String command = ""
    String args = ""

    String dockerImage = "ubuntu:14.04.1"
    String networkType = NetworkType.HOST

    List<Map<String, String>> volumes

    Float cpus = 1.5
    Float mem = 300

    Map<String, String> envs = new LinkedHashMap<String, String>()
    Integer instances = null
    List<Integer> ports

    def configFetcher = new GitHubFetcher()

    def buildRequestJson() {
        def json = new JsonBuilder()
        def root = json {
            id(marathon_id())
            container({
                type ContainerType.DOCKER
                docker(
                        image: this.dockerImage,
                        network: this.networkType,
                )
                volumes(VolumesHelper.build(this))
            })
            cpus(this.cpus)
            mem(this.mem)
            if (this.ports != null && !this.ports.isEmpty()){
                ports(this.ports)
            }
            if (!envs.isEmpty()) {
                env(envs)
            }
            if (this.instances != null){
                instances(this.instances)
            }
        }
        CommandHelper.build(this, root)
        return json
    }

    def marathon_id() {
        return "/" + [stage, project.group, project.name].join("/")
    }

    def postConfigToMarathon() {
        def client = new RESTClient(marathonURL)
        try {
            client.get(path: "/v2/apps/${marathon_id()}") {}
        } catch (HttpResponseException ex) {
            if (ex.statusCode != 404) {
                throw ex
            }
            logger.debug("Post to marathon")
            def requestBody = buildRequestJson().toString()
            logger.debug(requestBody)
            logger.debug(client.post(path: "/v2/apps", body: requestBody, requestContentType: ContentType.JSON))
        }
    }

    def processExternalConfig() {
        if (useExternalConfig && externalConfigSourcePerStage[stage]) {
            def cfg = configFetcher.fetchWikiaConfig(this)
            for (tuple in cfg) {
                def key = tuple[0]
                def val = tuple[1]
                if (!(key && val)) {
                    throw new TaskValidationException("external provided invalid value", [new InvalidUserDataException(key), new InvalidUserDataException(value)])
                }
                if (this.allowOverrideOfExternalConfig || !envs.containsKey(key)) {
                    this.envs[key] = val
                }
            }
        }
    }

    MarathonTask() {
        if (project.group == '') {
            throw new TaskValidationException("project.group needs to be set", [new InvalidUserDataException('project.group')])
        }
    }

    def validateData() {
        CommandHelper.validate(this)
        VolumesHelper.validate(this)
    }

    @TaskAction
    def sampleTask() {
        validateData()
        processExternalConfig()
    }
}