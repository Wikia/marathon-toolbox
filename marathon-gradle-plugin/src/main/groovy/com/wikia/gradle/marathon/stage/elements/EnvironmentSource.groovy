package com.wikia.gradle.marathon.stage.elements

import com.wikia.gradle.marathon.common.Validating
import mesosphere.marathon.client.model.v2.HealthCheck

import static com.wikia.gradle.marathon.common.ConfigResolver.dslToParamClosure

class EnvironmentSource implements Validating {
    List<Closure<HealthCheck>> healthcheckList = new LinkedList<>()

    public def fromConsulKV(String val){

    }

    public def fromConsulKV(Closure closure) {
        healthcheckList.add(dslToParamClosure(closure))
    }
}
