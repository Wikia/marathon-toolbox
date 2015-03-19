package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.App
import com.wikia.gradle.marathon.common.MarathonExtension
import com.wikia.gradle.marathon.common.Stage
import org.gradle.api.Plugin
import org.gradle.api.Project

class MarathonPlugin implements Plugin<Project> {

    private static final String IS_CONFIRMATION_NOT_NEEDED = "marathon.isConfirmationNotNeeded"
    private static final String deployConfirmation = "deployConfirmation"

    @Override
    void apply(Project project) {
        project.ext.Marathon = MarathonTask.class
        project.extensions.create("deployments", MarathonExtension)
        def isConfirmationNotNeeded = project.hasProperty(IS_CONFIRMATION_NOT_NEEDED).
                toString().toBoolean()

        project.afterEvaluate { evaluatedProject ->
            if (!isConfirmationNotNeeded) {
                evaluatedProject.task(deployConfirmation, type: ConfirmationTask) {}
            }
            def deployments = evaluatedProject.extensions.
                    getByName("deployments") as MarathonExtension
            deployments.getStageNames().each { stageName ->
                evaluatedProject.task("setup${stageName.capitalize()}", type: MarathonTask) {
                    group = "Marathon Setup"
                    description = "Setup configured stage: ${stageName}"
                    stage = deployments.getStage(stageName).validate()
                    if (!isConfirmationNotNeeded) {
                        dependsOn(deployConfirmation);
                    }
                }

                if (deployments.getStage(stageName).resolve(App).isMaven()) {
                    evaluatedProject.task("deploy${stageName.capitalize()}", type: MarathonTask) {
                        group = "Marathon Deployment"
                        description = "Deploy configured stage: ${stageName}"
                        stage = deployments.getStage(stageName).validate()
                        dependsOn(stage.resolve(App).mavenPublishTaskName)
                        if (!isConfirmationNotNeeded) {
                            dependsOn(deployConfirmation);
                        }
                    }
                }
            }
        }
    }
}
