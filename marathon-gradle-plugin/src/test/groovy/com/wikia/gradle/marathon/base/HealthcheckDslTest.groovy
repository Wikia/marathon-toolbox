package com.wikia.gradle.marathon.base

import com.wikia.gradle.marathon.stage.elements.Healthchecks
import org.junit.Test

import static org.junit.Assert.assertEquals

class HealthcheckDslTest {

    @Test
    public void dslCreatesProperHealthcheckConfiguration() {
        def hcs = new Healthchecks()
        Closure dsl = {
            healthcheck {
                portIndex = 2
                intervalSeconds = 10
                maxConsecutiveFailures = 11
                protocol = "HTTP"
                path = "/test"
            }
            healthcheck {
                portIndex = 1
                path = "/healthcheck"
            }
        }
        dsl.delegate = hcs
        dsl()
        def result = hcs.healthchecksProvider()
        assertEquals(result.size(), 2)
        assertEquals(result.get(0).gracePeriodSeconds, null)
        assertEquals(result.get(0).path, "/test")
        assertEquals(result.get(0).portIndex, 2)
        assertEquals(result.get(1).portIndex, 1)
        assertEquals(result.get(1).path, "/healthcheck")
    }
}
