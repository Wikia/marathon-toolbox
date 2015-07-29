package com.wikia.gradle.marathon.common

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class MarathonExtensionTest {

    @Test
    public void stageIsCreatedUsingNiceDsl() {
        def stageCreator = new MarathonExtension()
        stageCreator.globalDefaults {
            marathon {
                prodUrl = "ehlo"
                devUrl = "olhe"
            }
        }
        stageCreator.awesome_stage {
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
        def stage = stageCreator.getStage("awesome_stage").validate()

        assertEquals(stage.name, "awesome_stage")
        assertEquals(stage.resolve(Resources).cpus, 1, 0.001) // 0.001 tolerance
        assertEquals(stage.resolve(Resources).mem, 100, 0.001)
        assertEquals(stage.resolve(Environment).dslProvidedEnvironment.get("d")(), "someValue")
        assertEquals(stage.resolve(Environment).dslProvidedEnvironment.get("a")(), "b")
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
    void "Stages Config Can Be Inherited From Base Using Dev Marathon Address"() {
        def stageCreator = new MarathonExtension()
        stageCreator.globalDefaults {
            marathon {
                prodUrl = "A"
                devUrl = "V"
            }
        }

        Stage rawStage = stageCreator.anyRandomStage {
            name = "x"
        }

        def stage = stageCreator.getStage("x")
        stage.marathon {
        }

        assertNull(rawStage.resolve(MarathonAddress).url)
        assertEquals("x", stage.name)
        assertEquals("V", stage.resolve(MarathonAddress).url)
    }
    @Test
    void "Stages Config Can Be Inherited From Base Using Prod Marathon Address"() {
        def stageCreator = new MarathonExtension()
        stageCreator.globalDefaults {
            marathon {
                prodUrl = "A"
                devUrl = "V"
            }
        }

        Stage rawStage = stageCreator.anyRandomStage {
            name = "x"
        }

        def stage = stageCreator.getStage("x")
        stage.marathon {
            useProd = true
        }

        assertNull(rawStage.resolve(MarathonAddress).url)
        assertEquals("x", stage.name)
        assertEquals("A", stage.resolve(MarathonAddress).url)
    }

    @Test
    public void productionStageIsCreatedUsingNiceDsl() {
        def stageCreator = new MarathonExtension()
        def dsl = {
            globalDefaults {
                marathon {
                    url = "http://"
                }
                resources {
                    mem = 100
                    ports = [0]
                    instances = 2
                    cpus = 1.1
                }
                environment {
                    x = "1"
                }
            }
            production {
                resources {
                    mem = 200
                    instances = 10
                }
                environment {
                    y = "2"
                }
            }
            globalDefaults {
                resources {
                    cpus = 1.2
                }
            }
        }
        dsl.delegate = stageCreator
        dsl()
        def stage = stageCreator.getStage("production")

        assertEquals(stage.resolve(Environment).getEnv().get("x"), "1")
        assertEquals(stage.resolve(Environment).getEnv().get("y"), "2")

        assertEquals(stage.resolve(Resources).cpus, 1.2, 0.001)
        assertEquals(stage.resolve(Resources).mem, 200, 0.001)
        assertEquals(stage.resolve(Resources).instances, 10)
        assertEquals(stage.resolve(Resources).ports, [0])
    }

    @Test
    void stagesConfigShouldNotOverrideGlobalConfigForOtherStages() {
        def stageCreator = new MarathonExtension()
        def dsl = {
            globalDefaults {
                resources {
                    cpus = 10
                    mem = 10
                    useRandomPorts(2)
                }
            }
            testing {
                resources {
                    mem = 2
                }
            }
            production {
                resources {
                    cpus = 1
                    mem = 1
                }
            }

        }
        dsl.delegate = stageCreator
        dsl()
        def testing = stageCreator.getStage("testing")
        testing.insertBefore(stageCreator.globalDefaults)
        assertEquals(2, testing.resolve(Resources).mem, 0.001)
        assertEquals(10, testing.resolve(Resources).cpus, 0.001)

        def production = stageCreator.getStage("production")
        assertEquals(1, production.resolve(Resources).cpus, 0.001)
        assertEquals(Arrays.asList(0,0), production.resolve(Resources).ports)
    }
}
