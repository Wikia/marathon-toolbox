package com.wikia.gradle.consul

import org.junit.Test

import static groovy.util.GroovyTestCase.assertEquals

class SomeDsl {

    @Test
    void "sets url via simple dsl"() {
        def extension = new ConsulExtension()
        Closure dup = {
            url = "someurl"
            appName = "appN"
        }
        dup.delegate = extension;
        dup.call();

        assertEquals("someurl", extension.getUrl()())
        assertEquals("appN", extension.getAppName()())
    }

    @Test
    void "sets url via simple dsl with closure"() {
        def extension = new ConsulExtension()
        Closure dup = {
            url = { "someurl" }
            appName = { "appN" }
        }
        dup.delegate = extension;
        dup.call();

        assertEquals("someurl", extension.getUrl()())
        assertEquals("appN", extension.getAppName()())
    }
}
