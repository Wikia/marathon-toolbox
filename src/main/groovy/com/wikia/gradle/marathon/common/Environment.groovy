package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone

@AutoClone
class Environment {
    Map<String, Closure<String>> environmentStorage = new HashMap<>()

    def propertyMissing(String name, rawValue) {
        if (rawValue instanceof Closure) {
            environmentStorage[name] = rawValue
        } else {
            environmentStorage[name] = rawValue.toString()
        }
    }
}
