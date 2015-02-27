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
            def deployments = evaluatedProject.extensions.
                    getByName("deployments") as MarathonExtension
            deployments.stages.values().each { newStage ->
                evaluatedProject.task("setup${newStage.name.capitalize()}", type: MarathonTask) {
                    group = "Marathon Setup"
                    description = "Setup configured stage: ${newStage.name}"
                    stage = newStage
                    marathonExtension = deployments
                    deployments.resolve(stage).validate()
                }
                if (deployments.appConfig.isMaven()) {
                    evaluatedProject.
                            task("deploy${newStage.name.capitalize()}", type: MarathonTask) {
                                group = "Marathon Deployment"
                                description = "Deploy configured stage: ${newStage.name}"
                                stage = newStage
                                marathonExtension = deployments
                                dependsOn(deployments.appConfig.mavenPublishTaskName)
                            }
                }
            }
        }
    }
}
