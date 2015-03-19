package com.wikia.gradle.marathon

import org.gradle.api.BuildCancelledException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ConfirmationTask extends DefaultTask {

    @TaskAction
    def confirmation() {
        def console = System.console()
        if (console != null) {
            println "CONFIRMATION: type project name (" + project.name + ")";
            def line = console.readLine();
            if (!(line == project.name)) {
                throw new BuildCancelledException("aborted by user")
            }
        } else {
            println "deploy works only in console";
            throw new BuildCancelledException("deploy works only in console")
        }
    }
}
