package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone
import mesosphere.marathon.client.model.v2.HealthCheck

import static com.wikia.gradle.marathon.common.ConfigResolver.dslToParamClosure

@AutoClone
class Healthchecks implements Validating {

    List<Closure<HealthCheck>> healthcheckList = new LinkedList<>()

    @Deprecated
    public def reset() {
        healthcheckList.clear()
    }

    public def clear() {
        healthcheckList.clear()
    }

    public def healthcheck(Closure closure) {
        healthcheckList.add(dslToParamClosure(closure))
    }

    List<HealthCheck> healthchecksProvider() {
        List<HealthCheck> rv = new LinkedList<>()
        for (cHealthcheck in healthcheckList) {
            rv.add(cHealthcheck(new CloneableHealthcheck()))
        }
        return rv
    }
}
