package com.wikia.gradle.marathon.stage.elements

import com.wikia.gradle.marathon.base.Validating
import groovy.transform.AutoClone

@AutoClone
class Environment implements Validating {

    Map<String, Closure<String>> dslProvidedEnvironment = new HashMap<>()

    public Map<String, String> getEnv() {
        Map<String, String> rv = new HashMap<>()
        dslProvidedEnvironment.each { key, closure ->
            rv.put(key, closure())
        }
        return rv
    }

    def propertyMissing(String name, rawValue) {
        if (rawValue instanceof Closure) {
            dslProvidedEnvironment[name] = rawValue
        } else {
            dslProvidedEnvironment[name] = { rawValue.toString() }
        }
    }
}
