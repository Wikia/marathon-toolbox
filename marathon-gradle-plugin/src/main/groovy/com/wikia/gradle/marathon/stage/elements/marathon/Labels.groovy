package com.wikia.gradle.marathon.stage.elements.marathon

import com.wikia.gradle.marathon.common.Validating
import groovy.transform.AutoClone

@AutoClone
class Labels implements Validating {

    Map<String, Closure<String>> dslProvidedLabels = new HashMap<>()

    public Map<String, String> getLabels() {
        Map<String, String> rv = new HashMap<>()
        dslProvidedLabels.each { key, closure ->
            rv.put(key, closure())
        }
        return rv
    }

    def propertyMissing(String name, rawValue) {
        if (rawValue instanceof Closure) {
            dslProvidedLabels[name] = rawValue
        } else {
            dslProvidedLabels[name] = { rawValue.toString() }
        }
    }
}
