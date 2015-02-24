package com.wikia.gradle.marathon.common

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class MarathonExtensionTest {

    @Test
    public void stageIsCreatedUsingNiceDsl() {
        def stageCreator = new MarathonExtension()
        stageCreator.marathon {
            url = "ehlo"
        }
        def stage = stageCreator.newStage {
            name = "awesome_stage"
            resources {
                cpus = 1
                mem = 100
                instances = 1
                ports = [0]
            }
            environment {
                a = "b"
                c = "d"
                d = { "someValue" }
            }
        }
        stageCreator.resolve(stage).validate()

        assertEquals(stage.name, "awesome_stage")
        assertEquals(stage.resourcesConfig.cpus, 1, 0.001) // 0.001 tolerance
        assertEquals(stage.resourcesConfig.mem, 100, 0.001)
        assertEquals(stage.environmentConfig.environmentStorage.get("d")(), "someValue")
        assertEquals(stage.environmentConfig.environmentStorage.get("a"), "b")
    }

    @Test(expected = RuntimeException.class)
    void failsWhenMarathonConfigIsNotProperlySet() {
        def stageCreator = new MarathonExtension()
        stageCreator.marathon {
        }
        stageCreator.resources {
            cpus = 1
            mem = 100
            instances = 1
            ports = [0]
        }
        def stage = stageCreator.production {
        }
        stageCreator.resolve(stage).validate()
    }

    @Test
    void stagesConfigCanBeInheritedFromBase() {
        def stageCreator = new MarathonExtension()
        stageCreator.marathon {
            url = "A"
        }

        def stage = stageCreator.newStage {
            name = "x"
        }
        stage.marathon {
        }
        assertNull(stage.resolve().marathonConfig.url)
        assertEquals(stage.resolve().name, "x")
        assertEquals(stageCreator.resolve(stage).marathonConfig.url, "A")
    }

    @Test
    public void productionStageIsCreatedUsingNiceDsl() {
        def stageCreator = new MarathonExtension()
        stageCreator.marathon {
            url = "http://"
        }
        stageCreator.resources {
            mem = 100
            ports = [0]
            instances = 2
            cpus = 1.1
        }
        stageCreator.environment {
            x = "1"
        }
        def stage = stageCreator.production {
            resources {
                mem = 200
                instances = 10
            }
            environment {
                y = "2"
            }
        }
        stageCreator.resources {
            cpus = 1.2
        }
        stageCreator.resolve(stage).validate()
        assertEquals(stage.environmentConfig.environmentStorage.get("x"), "1")
        assertEquals(stage.environmentConfig.environmentStorage.get("y"), "2")

        assertEquals(stage.resourcesConfig.cpus, 1.2, 0.001)
        assertEquals(stage.resourcesConfig.mem, 200, 0.001)
        assertEquals(stage.resourcesConfig.instances, 10)
        assertEquals(stage.resourcesConfig.ports, [0])
    }
}
