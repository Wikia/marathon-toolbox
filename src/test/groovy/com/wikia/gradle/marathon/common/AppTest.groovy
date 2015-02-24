package com.wikia.gradle.marathon.common

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals

class AppTest {

    static final Project rootProject = ProjectBuilder.builder().withName("root").build()

    def testProject() {
        Project project = ProjectBuilder.builder()
                .withName("test_a")
                .withParent(rootProject)
                .build()
        project.ext.distZip = [extension: "zip", archiveName: "test-zzz.zip"]
        project.ext.applicationName = "appName"
        project
    }

    @Test
    void "test dsl generating proper arguments for Dropwizard application"() {
        def app = new App()
        def dsl = {
            mavenSource("http://repo-url")
            dropwizardApplication("config-name")
        }
        dsl.delegate = app
        dsl()

        GroovyTestCase.assertEquals(app.getArguments()(testProject()),
                                    ["server", "test-zzz/conf/config-name"])
    }

    @Test
    void "test dsl generating proper uri for default application"() {
        def app = new App()
        def dsl = {
            mavenSource("https://oss.sonatype.org/content/groups/google-with-staging")
        }
        dsl.delegate = app
        dsl()
        assertEquals(
                app.getUri()(testProject()),
                "https://oss.sonatype.org/content/groups/google-with-staging/root/test_a/unspecified/test_a-unspecified.jar"
        )
    }

    @Test
    void "test dsl generating proper command for Dropwizard application"() {
        def app = new App()
        def dsl = {
            mavenSource("https://oss.sonatype.org/content/groups/google-with-staging")
            dropwizardApplication("config-name")
        }
        dsl.delegate = app
        dsl()
        GroovyTestCase.assertEquals(app.cmd(testProject()),
                                    "test-zzz/bin/appName server test-zzz/conf/config-name")
    }
}
