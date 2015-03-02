package com.wikia.gradle.marathon.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class MarathonExtension {
    Logger logger = LoggerFactory.getLogger(MarathonExtension)
    Stage globalDefaults = new Stage()

    private Map<String, Stage> stages = new HashMap<>()

    def globalDefaults(Closure closure) {
        closure.delegate = this.globalDefaults
        closure()
    }


    def methodMissing(String name, args) {
        Object[] varArgs = args
        if (args.size() == 1 && varArgs[0] instanceof Closure) {
            Stage stage = new Stage()
            stage.name = name
            setupAndAddStage(stage, varArgs[0] as Closure)
        } else {
            throw new RuntimeException("Deployment configuration must be declared using single Closure")
        }
    }

    def propertyMissing(String name, dslClosure) {
        if (dslClosure instanceof Closure) {
            Stage stage = new Stage()
            stage.name = name
            setupAndAddStage(stage, dslClosure)
            return stage
        } else {
            throw new RuntimeException("Deployment configuration must be declared using Closure")
        }
    }

    Stage getStage(String name){
        Stage rv = this.stages.get(name)
        rv.clone().insertBefore(this.globalDefaults)
    }

    Set<String> getStageNames() {
        return stages.keySet()
    }

    private def setupAndAddStage(Stage stage, Closure closure) {
        closure.delegate = stage
        closure()
        if (stage.name == null) {
            throw new RuntimeException("Stage.name is missing")
        }
        if (stages.containsKey(stage.name)) {
            stage.insertBefore(stages.get(stage.name))
        }
        stages.put(stage.name, stage)
        return stage
    }
}
