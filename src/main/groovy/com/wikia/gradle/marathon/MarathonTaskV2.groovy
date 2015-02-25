package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.MarathonExtension
import com.wikia.gradle.marathon.common.Stage
import mesosphere.marathon.client.Marathon
import mesosphere.marathon.client.MarathonClient
import mesosphere.marathon.client.model.v2.App
import mesosphere.marathon.client.model.v2.Container
import mesosphere.marathon.client.model.v2.HealthCheck
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MarathonTaskV2 extends DefaultTask {

    Stage stage
    MarathonExtension marathonExtension

    String getDeploymentId() {
        return "/xx/" + [stage.name, project.group, project.name].join("/")
    }

    App prepareAppDescription() {
        def app = new App()
        app.setPorts(this.stage.resourcesConfig.ports)
        app.setCpus(this.stage.resourcesConfig.cpus)
        app.setMem(this.stage.resourcesConfig.mem)
        app.setInstances(this.stage.resourcesConfig.instances)
        app.setEnv(this.stage.environmentConfig.getEnv())
        app.setId(this.getDeploymentId())

        def hc = new HealthCheck()
        hc.intervalSeconds = 5
        hc.portIndex = 1 // if app is dropwizard
        hc.path = "/healthcheck"
        app.setHealthChecks(Arrays.asList(hc))

        def appConfig = this.marathonExtension.appConfig
        if (appConfig.isDocker()) {
            Container container = new Container()
            container.type = "DOCKER"
            container.docker.image = appConfig.getImage()(project)
            container.docker.network = "HOST"
            app.setContainer(container)
        } else {
            app.setUris(Arrays.asList(appConfig.getUri()(project)))
        }

        app.setCmd(appConfig.getCmd()(project))
        app
    }

    @TaskAction
    def setupApp() {
        this.marathonExtension.resolve(this.stage)
        this.marathonExtension.validate(this.stage)
        Marathon marathon = MarathonClient.getInstance(this.stage.marathonConfig.url)
        marathon.createApp(prepareAppDescription())
    }
}
