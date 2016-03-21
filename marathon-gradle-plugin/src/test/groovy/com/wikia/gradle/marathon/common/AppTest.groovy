package com.wikia.gradle.marathon.common

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static GroovyTestCase.assertEquals

class AppTest {

    static final Project rootProject = ProjectBuilder.builder().withName("root").build()

    def testProject() {
        Project project = ProjectBuilder.builder()
                .withName("test_a")
                .withParent(rootProject)
                .build()
        project.ext.distZip = [extension: "zip", archiveName: "test-zzz.zip"]
        project.ext.applicationName = "appName"
        return project
    }

    @Test
    void "test dsl generating proper arguments for Dropwizard application"() {
        def app = new App()
        def dsl = {
            mavenSource("http://repo-url", "taskDependency")
            dropwizardApplication("config-name")
        }
        dsl.delegate = app
        dsl()

        assertEquals(app.getArguments()(testProject()),
                                    ["server", "test-zzz/conf/config-name"])
    }

    @Test
    void "test dsl generating proper uri for default application"() {
        def app = new App()
        def dsl = {
            mavenSource("https://oss.sonatype.org/content/groups/google-with-staging", "dependency")
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
            mavenSource("https://oss.sonatype.org/content/groups/google-with-staging",
                        "taskDependency")
            dropwizardCommand("server", "config-name")
        }
        dsl.delegate = app
        dsl()
        assertEquals(app.cmd(testProject()),
                                    "test-zzz/bin/appName server test-zzz/conf/config-name")
    }


    @Test
    void "dsl generating proper command for Dropwizard application with default config name"() {
        def app = new App()
        def dsl = {
            mavenSource("https://oss.sonatype.org/content/groups/google-with-staging",
                        "taskDependency")
            dropwizardCommand("server")
        }
        dsl.delegate = app
        dsl()
        assertEquals(app.cmd(testProject()),
                                    "test-zzz/bin/appName server test-zzz/conf/test_a.yaml")
    }

    @Test
    void "dsl generating proper command for Dropwizard based command with classpath config"() {
        def app = new App()
        def dsl = {
            mavenSource("https://oss.sonatype.org/content/groups/google-with-staging",
                        "taskDependency")
            dropwizardCommandClasspathConfig("servers")
        }
        dsl.delegate = app
        dsl()
        assertEquals(app.cmd(
                testProject()), "test-zzz/bin/appName servers classpath:test_a.yaml")
    }
}
