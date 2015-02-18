package com.wikia.gradle.marathon.helpers

import org.gradle.api.GradleException

class CommandHelper {
    static validate(task) {
        if (task.command != null && task.args != null) {
            throw new GradleException("Both Command and Args cannot be specified")
        }
    }

    static build(task, jsonRoot) {
        if (task.command != null) {
            jsonRoot['cmd'] = task.command
        }
        if (task.args != null) {
            jsonRoot['args'] = task.args
        }
    }
}
