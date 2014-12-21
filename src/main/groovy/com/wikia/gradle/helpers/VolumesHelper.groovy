package com.wikia.gradle.helpers

import org.gradle.api.GradleException

class VolumesHelper {
    static String CONTAINER_PATH = 'containerPath'
    static String HOST_PATH = 'hostPath'
    static String PATH = 'path'
    static String MODE = 'mode'

    static validate(task) {
        for (e in task.volumes) {
            if (e[PATH] != null) {
                e[CONTAINER_PATH] = e[PATH];
                e[HOST_PATH] = e[PATH];
            }

            if (e[CONTAINER_PATH] == null || e[HOST_PATH] == null) {
                throw new GradleException("Docker volumes paths ${CONTAINER_PATH} and ${HOST_PATH} or ${PATH} need to be set")
            }

            if (e[MODE] == null) {
                throw new GradleException("Volume mode needs to be set")
            }
        }
    }

    static build(task) {
        def result = []
        for (e in task.volumes) {
            if (e[PATH] != null) {
                e[CONTAINER_PATH] = e[PATH];
                e[HOST_PATH] = e[PATH];
            }

            result.add([
                    containerPath: e[CONTAINER_PATH],
                    hostPath     : e[HOST_PATH],
                    mode         : e[MODE]
            ])
        }
        println result
        return result
    }
}
