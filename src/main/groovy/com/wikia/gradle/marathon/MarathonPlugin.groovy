package com.wikia.gradle.marathon

import org.gradle.api.Plugin
import org.gradle.api.Project

class MarathonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.ext.Marathon = MarathonTask.class
    }
}
