package com.wikia.gradle.helpers

import org.gradle.api.GradleException

class CommandHelper {
    static validate(task) {
        if (task.command && task.args) {
            throw new GradleException("Both Command and Args cannot be specified")
        } else if (!(task.command || task.args)) {
            throw new GradleException("Please specify either command or args")
        }
    }

    static build(task, jsonRoot) {
        if (task.command) {
            jsonRoot['cmd'] = task.command
        } else if (task.args) {
            jsonRoot['args'] = task.args
        }
    }
}
