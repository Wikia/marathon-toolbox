package com.wikia.gradle.marathon

import com.wikia.gradle.marathon.common.StageCreator
import org.gradle.api.Plugin
import org.gradle.api.Project

class MarathonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.ext.Marathon = MarathonTask.class
        project.extensions.create("marathon", StageCreator)
//        project.task('productionDeploy'). << {
//            project.marathon.
    }

}
