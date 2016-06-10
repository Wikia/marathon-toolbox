package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.*
import mesosphere.marathon.client.model.v2.App
import mesosphere.marathon.client.model.v2.Container
import mesosphere.marathon.client.model.v2.HealthCheck
import mesosphere.marathon.client.model.v2.Resource
import org.gradle.api.Project

import java.util.stream.Collectors
import java.util.stream.Stream

class AppFactory {

    Stage stage
    Project project

    AppFactory(Stage stage, Project project) {
        this.stage = stage;
        this.project = project;
    }

    String getDefaultDeploymentId() {
        return "/" + [stage.name, project.group, project.name].join("/")
    }

    public App create() {
        def app = App.builder()

        def res = stage.resolve(Resources)
        def marathon = stage.resolve(Marathon);

        app.ports(res.ports)
        app.cpus(res.cpus)
        app.mem(res.mem)
        app.instances(res.instances)
        app.upgradeStrategy(marathon.resolveUpgradeStrategy());
        app.env(stage.resolve(Environment).getEnv())
        app.id(marathon.id ?: getDefaultDeploymentId())

        app.requirePorts(res.requirePorts)
        app.labels(marathon.resolveLabels().labels)

        app.backoffFactor(marathon.backoffFactor)
        app.backoffSeconds(marathon.backoffSeconds)
        app.maxLaunchDelaySeconds(marathon.maxLaunchDelaySeconds)

        List<HealthCheck> healthChecks = stage.resolve(Healthchecks).healthchecksProvider()
        app.healthChecks(healthChecks)

        List<List<String>> constraints = stage.resolve(Constraints).getConstraints()
        app.constraints(constraints)

        def appConfig = stage.resolve(com.wikia.gradle.marathon.common.App)
        appConfig.validate()
        if (appConfig.isDocker()) {
            Container container = new Container()
            container.type = "DOCKER"
            container.docker.image = appConfig.imageProvider(project)
            container.docker.network = "HOST"
            app.container(container)
        } else {
            app.fetch(Stream.of(appConfig.uriProvider(project)).map({ uri ->
                Resource.builder()
                        .uri(uri)
                        .extract(appConfig.extractFetched)
                        .cache(appConfig.cacheFetching)
                        .build()
            }).collect(Collectors.toList()))
        }

        app.cmd(appConfig.cmdProvider(project))
        return app.build()
    }
}
