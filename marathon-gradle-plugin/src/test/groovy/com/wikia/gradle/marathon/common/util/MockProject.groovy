package com.wikia.gradle.marathon.common.util

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class MockProject {
    static final Project ROOT_PROJECT = ProjectBuilder.builder().withName("root").build()

    static def testProject(){
        return testProject(ROOT_PROJECT);
    }

    static def testProject(Project rootProject) {
        Project project = ProjectBuilder.builder()
                .withName("test_a")
                .withParent(rootProject)
                .build()
        project.ext.distZip = [extension: "zip", archiveName: "test-zzz.zip"]
        project.ext.applicationName = "appName"
        return project
    }

}
