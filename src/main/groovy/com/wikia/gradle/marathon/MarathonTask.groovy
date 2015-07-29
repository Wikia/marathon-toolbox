package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.*
import mesosphere.marathon.client.Marathon
import mesosphere.marathon.client.MarathonClient
import mesosphere.marathon.client.model.v2.App
import mesosphere.marathon.client.model.v2.Container
import mesosphere.marathon.client.model.v2.HealthCheck
import mesosphere.marathon.client.utils.MarathonException
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class MarathonTask extends DefaultTask {
    public static final String FORCE_UPDATE = 'marathon.forceUpdate'
    public static final String PRESERVE_INSTANCE_ALLOCATION = 'marathon.preserveInstanceAllocation'
    Stage stage

    String getDeploymentId() {
        return "/" + [stage.name, project.group, project.name].join("/")
    }

    App prepareAppDescription() {
        def app = new App()
        def res = this.stage.resolve(Resources)
        app.setPorts(res.ports)
        app.setCpus(res.cpus)
        app.setMem(res.mem)
        app.setInstances(res.instances)
        app.setEnv(this.stage.resolve(Environment).getEnv())
        app.setId(this.getDeploymentId())

        List<HealthCheck> healthChecks = this.stage.resolve(Healthchecks).healthchecksProvider()

        if (healthChecks.size() > 0) {
            app.setHealthChecks(healthChecks)
        }

        def appConfig = this.stage.resolve(com.wikia.gradle.marathon.common.App)
        appConfig.validate()
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
        return app
    }

    static Optional<App> attemptGetExistingApp(Marathon client, String appId) {
        def app
        try {
            app = Optional.<App> of(client.getApp(appId).getApp())
        } catch (MarathonException ex) {
            getLogger().info("exception encountered while fetching data for app", ex)
            app = Optional.<App> empty()
        }
        return app
    }

    static def mergeAppDescriptions(Optional<App> existingApp, App appDescription,
                                    Project project) {

        if (existingApp.isPresent()){
            if (project.hasProperty(PRESERVE_INSTANCE_ALLOCATION) &&
                project.property(PRESERVE_INSTANCE_ALLOCATION).toString().toBoolean()) {
                appDescription.instances = existingApp.get().instances;
            }
        }
        return appDescription
    }

    @TaskAction
    def setupApp() {
        this.stage = stage.validate()
        Marathon marathon = MarathonClient.getInstance(this.stage.resolve(MarathonAddress).getUrl())
        def appDescription = prepareAppDescription()

        def existingApp = attemptGetExistingApp(marathon, appDescription.getId())
        if (existingApp.isPresent()) {
            appDescription = mergeAppDescriptions(existingApp, appDescription, project)
            Boolean force = Optional.ofNullable(project.getProperties().get(FORCE_UPDATE))
                    .map({x -> x.toString().toBoolean()})
                    .orElse(false)

            marathon.updateApp(appDescription.getId(), appDescription, force)
        } else {
            marathon.createApp(appDescription)
        }
    }
}
