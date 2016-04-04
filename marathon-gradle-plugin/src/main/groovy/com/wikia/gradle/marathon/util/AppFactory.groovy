package com.wikia.gradle.marathon.util

import com.wikia.gradle.marathon.base.Stage
import com.wikia.gradle.marathon.stage.elements.Healthchecks
import com.wikia.gradle.marathon.stage.elements.Marathon
import com.wikia.gradle.marathon.stage.elements.Resources
import mesosphere.marathon.client.model.v2.App
import mesosphere.marathon.client.model.v2.HealthCheck

class AppFactory {

    List<Closure> closureList = new LinkedList<>();

    AppFactory add(Closure cl) {
        closureList.add(cl)
        return this
    }

    App produce() {
        def app = new App()
        closureList.each {
            it.call(app)
        }
        return app
    }

    AppFactory withResources(Resources res) {
        return add { app ->
            app.setPorts(res.ports)
            app.setCpus(res.cpus)
            app.setMem(res.mem)
            app.setInstances(res.instances)
            app.setRequirePorts(res.requirePorts)
        }
    }

    AppFactory withMarathon(Marathon marathon) {
        return add { app ->
            app.setUpgradeStrategy(marathon.resolveUpgradeStrategy());
            app.setLabels(marathon.resolveLabels().labels)
            app.setBackoffFactor(marathon.backoffFactor)
            app.setBackoffSeconds(marathon.backoffSeconds)
            app.setMaxLaunchDelaySeconds(marathon.maxLaunchDelaySeconds)
        }
    }

    AppFactory withHealthChecks(Collection<HealthCheck> list){
        return add { App app ->
            app.setHealthChecks()
        }
    }

    AppFactory withStage(Stage stage) {
        return this
                .withResources(stage.resolve(Resources))
                .withMarathon(stage.resolve(Marathon))
    }

}
