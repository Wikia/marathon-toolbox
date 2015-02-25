package com.wikia.gradle.marathon.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class MarathonExtension {

    Logger logger = LoggerFactory.getLogger(MarathonExtension)
    Stage baseStage = new Stage()
    App appConfig = new App()
    Healthchecks healthchecks = new Healthchecks()

    private Map<String, Stage> stages = new HashMap<>()

    def resources(Closure closure) {
        baseStage.resources(closure)
    }

    def marathon(Closure closure) {
        baseStage.marathon(closure)
    }

    def app(Closure closure) {
        closure.delegate = appConfig
        closure()
    }

    def healthchecks(Closure closure) {
        closure.delegate = healthchecks
        closure
    }

    def environment(Closure closure) {
        baseStage.environment(closure)
    }

    def newStage(Closure closure) {
        return setupAndAddStage(new Stage(), closure)
    }

    def production(Closure closure) {
        def stage = new Stage()
        stage.name = "production"
        return setupAndAddStage(stage, closure)
    }

    def integration(Closure closure) {
        def stage = new Stage()
        stage.name = "integration"
        return setupAndAddStage(stage, closure)
    }

    def testing(Closure closure) {
        def stage = new Stage()
        stage.name = "testing"
        return setupAndAddStage(stage, closure)
    }

    def Map<String, Stage> getStages() {
        return this.stages
    }

    private def setupAndAddStage(Stage stage, Closure closure) {
        closure.delegate = stage
        closure()
        if (stage.name == null) {
            throw new RuntimeException("Stage.name is missing")
        }
        if (stages.containsKey(stage.name)) {
            logger.warn("Overwriting stage ${stage.name} configuration")
        }
        stages.put(stage.name, stage)
        return stage
    }

    def resolve(Stage stage) {
        this.baseStage.resolve()
        stage.resolve(this.baseStage)
        return stage
    }

    def validate(Stage stage){
        stage.validate()
        appConfig.validate()
    }
}
