package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.Stage
import mesosphere.marathon.client.Marathon
import mesosphere.marathon.client.MarathonClient
import mesosphere.marathon.client.model.v2.App
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MarathonTaskV2 extends DefaultTask {

    Marathon marathon = MarathonClient.getInstance(this.stage.marathonConfig.url)
    Stage stage

    String getDeploymentId() {
        return "/" + [stage.name, project.group, project.name].join("/")
    }

    App prepareAppDescription(){
        def app = new App()
        app.setPorts(this.stage.resourcesConfig.ports)
        app.setCpus(this.stage.resourcesConfig.cpus)
        app.setMem(this.stage.resourcesConfig.mem)
        app.setInstances(this.stage.resourcesConfig.instances)
        app.setEnv(this.stage.environmentConfig.environmentStorage)
        app.setId(this.getDeploymentId())
        app.setCmd()
    }

    @TaskAction
    def setupApp() {
        marathon.createApp(prepareAppDescription())
    }
}
