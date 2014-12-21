package com.wikia.gradle

import com.github.zafarkhaja.semver.Version
import org.gradle.api.Project
import org.gradle.api.tasks.TaskInstantiationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class MarathonTest {
    static prepareTask(Project project, name = 'ha') {
        MarathonTask task = project.task('ha', type: MarathonTask)
        task.configFetcher = [fetchWikiaConfig: { t-> return [['A', 1], ['B', 3]] }] as GitHubFetcher
        // mock external services
        task
    }

    static prepareFilledTask(Project project, name = 'ha') {
        MarathonTask task = prepareTask(project, name)
        task.command = 'test'
        task.volumes = [
                [path: "/dev/logger", mode: MarathonTask.VolumeMode.RW]
        ]
        return task
    }

    @Test
    public void taskIsInstantiatiedNormally() {
        Project project = ProjectBuilder.builder().build()
        project.group = "com.wikia"
        def task = prepareFilledTask(project)
        assertTrue(task instanceof MarathonTask)
    }

    @Test(expected = TaskInstantiationException)
    public void noProjectGroupWillcauseExceptionToBeThrown() {
        Project project = ProjectBuilder.builder().build()
        prepareFilledTask(project)
    }

    @Test
    public void fetching() {
        Project project = ProjectBuilder.builder().build()
        project.group = "com.wikia"
        MarathonTask task = prepareFilledTask(project)

        task.execute()
    }

    @Test
    public void buildsProperJsonForSampleConfigs() {
        def properJson = '{"id":"/test/com.wikia/test","container":{"type":"DOCKER","docker":{"image":"ubuntu:14.04.1","network":"HOST"},"volumes":[{"containerPath":"/dev/logger","hostPath":"/dev/logger","mode":"RW"}]},"cmd":"test"}'
        Project project = ProjectBuilder.builder().build()
        project.group = "com.wikia"
        MarathonTask task = prepareFilledTask(project)
        assertEquals(task.buildRequestJson().toString(), properJson)

        // validate json after processing external config
        task.processExternalConfig()
        properJson = '{"id":"/test/com.wikia/test","container":{"type":"DOCKER","docker":{"image":"ubuntu:14.04.1","network":"HOST"},"volumes":[{"containerPath":"/dev/logger","hostPath":"/dev/logger","mode":"RW"}]},"env":{"A":1,"B":3},"cmd":"test"}'
        assertEquals(task.buildRequestJson().toString(), properJson)
    }

    @Test
    public void aaa() {
        def x1 = Version.valueOf("1.20.2")
        println x1.compareTo(Version.valueOf("1.2.1"))
    }
}