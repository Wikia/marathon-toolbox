package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.Constraints
import com.wikia.gradle.marathon.common.Environment
import com.wikia.gradle.marathon.common.Healthchecks
import com.wikia.gradle.marathon.common.Marathon
import com.wikia.gradle.marathon.common.Resources
import com.wikia.gradle.marathon.common.Stage
import mesosphere.marathon.client.model.v2.App
import mesosphere.marathon.client.model.v2.Container
import mesosphere.marathon.client.model.v2.HealthCheck
import org.gradle.api.Project

class AppFactory {
    Stage stage
    Project project

    AppFactory(Stage stage, Project project){
        this.stage = stage;
        this.project = project;
    }
    String getDefaultDeploymentId() {
        return "/" + [stage.name, project.group, project.name].join("/")
    }

    public App create(){
        def app = new App()
        def res = stage.resolve(Resources)
        def marathon = stage.resolve(Marathon);
        app.setPorts(res.ports)
        app.setCpus(res.cpus)
        app.setMem(res.mem)
        app.setInstances(res.instances)
        app.setUpgradeStrategy(marathon.resolveUpgradeStrategy());
        app.setEnv(stage.resolve(Environment).getEnv())
        app.setId(marathon.id ?: getDefaultDeploymentId())

        app.setRequirePorts(res.requirePorts)
        app.setLabels(marathon.resolveLabels().labels)

        app.setBackoffFactor(marathon.backoffFactor)
        app.setBackoffSeconds(marathon.backoffSeconds)
        app.setMaxLaunchDelaySeconds(marathon.maxLaunchDelaySeconds)

        List<HealthCheck> healthChecks = stage.resolve(Healthchecks).healthchecksProvider()
        app.setHealthChecks(healthChecks)

        List<List<String>> constraints = stage.resolve(Constraints).getConstraints()
        app.setConstraints(constraints)

        def appConfig = stage.resolve(com.wikia.gradle.marathon.common.App)
        appConfig.validate()
        if (appConfig.isDocker()) {
            Container container = new Container()
            container.type = "DOCKER"
            container.docker.image = appConfig.imageProvider(project)
            container.docker.network = "HOST"
            app.setContainer(container)
        } else {
            app.setUris(Collections.<String>singletonList(appConfig.uriProvider(project)))
        }

        app.setCmd(appConfig.cmdProvider(project))
        return app
    }
}
