package com.wikia.gradle.marathon.base

import com.wikia.gradle.marathon.stage.elements.marathon.Constraints
import org.junit.Test

import static org.junit.Assert.assertEquals

class ConstraintsDslTest {

    @Test
    public void dslCreatesProperConstraintsConfiguration() {
        def constraints = new Constraints()
        Closure dsl = {
            constraint("hostname", "GROUP_BY")
            constraint("hostname", "UNIQUE")
            constraint("rack_id", "GROUP_BY", "3")
        }
        dsl.delegate = constraints
        dsl()
        def result = constraints.getConstraints()
        assertEquals(3, result.size())
        assertEquals(["hostname", "GROUP_BY"], result[0])
        assertEquals(["hostname", "UNIQUE"], result[1])
        assertEquals(["rack_id", "GROUP_BY", "3"], result[2])
    }
}
