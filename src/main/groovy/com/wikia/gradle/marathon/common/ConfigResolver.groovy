package com.wikia.gradle.marathon.common

class ConfigResolver {

    static Closure dslToParamClosure(Closure closure) {
        return { param ->
            closure.delegate = param
            closure()
            param
        }
    }

    static Closure stackClosures(Closure newClosure, Closure oldClosure) {
        if (oldClosure == null) {
            return newClosure
        } else {
            return { param ->
                newClosure(oldClosure(param))
            }
        }
    }

    static Closure noop() {
        return { param -> param }
    }
}
