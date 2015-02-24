package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.MarathonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MarathonPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.ext.Marathon = MarathonTask.class
        project.extensions.create("deployments", MarathonExtension)

        project.afterEvaluate { evaluatedProject ->
            def deployments = evaluatedProject.extensions.getByName("deployments") as MarathonExtension
            deployments.stages.values().each { newStage ->
                evaluatedProject.task("deploy${newStage.name.capitalize()}", type: MarathonTaskV2) {
                    group = "deployment"
                    description = "Deploy to ${newStage.name}"
                    stage = newStage
                    deployments.resolve(stage).validate()
                }
            }
        }
    }
}
