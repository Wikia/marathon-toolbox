package com.wikia.gradle.marathon.stage.elements

import com.wikia.gradle.marathon.base.CloneableHealthcheck
import com.wikia.gradle.marathon.base.Validating
import groovy.transform.AutoClone
import mesosphere.marathon.client.model.v2.HealthCheck

import static com.wikia.gradle.marathon.base.ConfigResolver.dslToParamClosure

@AutoClone
class Healthchecks implements Validating {

    List<Closure<HealthCheck>> healthcheckList = new LinkedList<>()

    public def reset() {
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
