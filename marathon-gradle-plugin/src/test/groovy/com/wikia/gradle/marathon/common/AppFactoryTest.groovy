package com.wikia.gradle.marathon.common

import com.wikia.gradle.marathon.AppFactory
import com.wikia.gradle.marathon.common.util.MockProject
import org.junit.Test

import static org.junit.Assert.assertEquals

class AppFactoryTest {
    
    @Test
    public void testSimplestStageDefintionCreatesProperAppDefinition() {
        def stageCreator = new MarathonExtension();
        stageCreator.prod {
            app {
                mavenSource("http://repo-url", "taskDependency")
                dropwizardApplication("config-name")
            }
            marathon {
                marathonUrl = "ehlo"
            }
        }
        def appFactory = new AppFactory(stageCreator.getStage("prod"), MockProject.testProject());
        def app = appFactory.create()
        assertEquals("/prod/root/test_a", app.id)
        assertEquals("test-zzz/bin/appName server test-zzz/conf/config-name", app.cmd)
        assertEquals("http://repo-url/root/test_a/unspecified/test_a-unspecified.zip",
                     app.uris.first())
    }
}
