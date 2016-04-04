package com.wikia.gradle.marathon.base

import groovy.transform.AutoClone
import mesosphere.marathon.client.model.v2.HealthCheck

@AutoClone
class CloneableHealthcheck extends HealthCheck {
}
