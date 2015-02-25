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
        return "/" + [stage.name, project.group, project.name].join("/")
    }

    App prepareAppDescription() {
        def app = new App()
        app.setPorts(this.stage.resourcesConfig.ports)
        app.setCpus(this.stage.resourcesConfig.cpus)
        app.setMem(this.stage.resourcesConfig.mem)
        app.setInstances(this.stage.resourcesConfig.instances)
        app.setEnv(this.stage.environmentConfig.getEnv())
        app.setId(this.getDeploymentId())

        List<HealthCheck> healthChecks = this.marathonExtension.healthchecks.healthchecksProvider()

        if (healthChecks.size() > 0) {
            app.setHealthChecks(healthChecks)
        }

        def appConfig = this.marathonExtension.appConfig
        if (appConfig.isDocker()) {
            Container container = new Container()
            container.type = "DOCKER"
            container.docker.image = appConfig.imageProvider(project)
            container.docker.network = "HOST"
            app.setContainer(container)
        } else {
            app.setUris(Arrays.asList(appConfig.uriProvider(project)))
        }

        app.setCmd(appConfig.cmdProvider(project))
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
