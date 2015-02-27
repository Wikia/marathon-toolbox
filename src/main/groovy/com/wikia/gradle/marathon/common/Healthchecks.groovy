package com.wikia.gradle.marathon.common

import mesosphere.marathon.client.model.v2.HealthCheck

import static com.wikia.gradle.marathon.common.ConfigResolver.dslToParamClosure

class Healthchecks {
    List<Closure<HealthCheck>> healthcheckList = new LinkedList<>()

    public def healthcheck(Closure closure) {
        healthcheckList.add(dslToParamClosure(closure))
    }

    List<HealthCheck> healthchecksProvider() {
        List<HealthCheck> rv = new LinkedList<>()
        for (cHealthcheck in healthcheckList) {
            rv.add(cHealthcheck(new HealthCheck()))
        }
        return rv
    }
}
