package com.wikia.gradle.marathon.common

import org.junit.Test

import static com.wikia.gradle.marathon.common.ConfigResolver.dslToParamClosure
import static com.wikia.gradle.marathon.common.ConfigResolver.stackClosures
import static org.junit.Assert.assertEquals


class ConfigResolverTest {

    @Test
    public void testObjectImmutabilityIsPreservedByDslClosures() {
        def cljA = dslToParamClosure {
            cpus = 1
            mem = 2
        }
        def cljB = stackClosures(dslToParamClosure {
            cpus = 2
        }, cljA)
        def base = new Resources()
        base.cpus = 10
        base.mem = 1
        Resources resA = cljA(base)
        Resources resB = cljB(base)

        assertEquals(1, resA.cpus, 0.1)
        assertEquals(2, resB.cpus, 0.1)
        assertEquals(10, base.cpus, 0.1)

        assertEquals(2, resA.mem, 0.1)
        assertEquals(2, resB.mem, 0.1)
        assertEquals(1, base.mem, 0.1)
    }
}
