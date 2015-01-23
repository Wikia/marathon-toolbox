package com.wikia.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class MarathonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.ext.Marathon = MarathonTask.class
    }
}
