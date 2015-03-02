package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.App
import com.wikia.gradle.marathon.common.MarathonExtension
import com.wikia.gradle.marathon.common.Stage
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
            deployments.getStageNames().each { stageName ->
                evaluatedProject.task("setup${stageName.capitalize()}", type: MarathonTask) {
                    group = "Marathon Setup"
                    description = "Setup configured stage: ${stageName}"
                    stage = deployments.getStage(stageName).validate()
                }

                if (deployments.getStage(stageName).resolve(App).isMaven()) {
                    evaluatedProject.
                            task("deploy${stageName.capitalize()}", type: MarathonTask) {
                                group = "Marathon Deployment"
                                description = "Deploy configured stage: ${stageName}"
                                stage = deployments.getStage(stageName).validate()
                                dependsOn(stage.resolve(App).mavenPublishTaskName)
                            }
                }
            }
        }
    }
}
