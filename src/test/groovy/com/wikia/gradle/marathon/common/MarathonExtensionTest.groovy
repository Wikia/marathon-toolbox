package com.wikia.gradle.marathon.common

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class MarathonExtensionTest {
    private static final double DELTA = 1e-15;

    @Test
    public void stageIsCreatedUsingNiceDsl() {
        def stageCreator = new MarathonExtension()
        stageCreator.globalDefaults {
            marathon {
                prodUrl = "ehlo"
                devUrl = "olhe"
                backoffFactor = 1.1
                backoffSeconds = 1
                maxLaunchDelaySeconds = 10
                upgradeStrategy {
                    minimumHealthCapacity = 0.5
                    maximumOverCapacity = 0.5
                }
                labels = {
                    foo = "bar"
                    one = "two"
                    consul = "true"
                }
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
        assertEquals(stage.resolve(Resources).cpus, 1, DELTA)
        assertEquals(stage.resolve(Resources).mem, 100, DELTA)
        assertEquals(stage.resolve(Environment).dslProvidedEnvironment.get("d")(), "someValue")
        assertEquals(stage.resolve(Environment).dslProvidedEnvironment.get("a")(), "b")
        assertEquals(stage.resolve(Marathon).resolveUpgradeStrategy().minimumHealthCapacity, 0.5, DELTA)
        assertEquals(stage.resolve(Marathon).resolveUpgradeStrategy().maximumOverCapacity, 0.5, DELTA)
        assertEquals(stage.resolve(Marathon).resolveLabels().labels.get("foo"), "bar")
        assertEquals(stage.resolve(Marathon).resolveLabels().labels.get("one"), "two")
        assertEquals(stage.resolve(Marathon).resolveLabels().labels.get("consul"), "true")
        assertEquals(stage.resolve(Marathon).backoffFactor, 1.1, DELTA)
        assertEquals(stage.resolve(Marathon).backoffSeconds, 1)
        assertEquals(stage.resolve(Marathon).maxLaunchDelaySeconds, 10)
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

        assertNull(rawStage.resolve(Marathon).url)
        assertEquals("x", stage.name)
        assertEquals("V", stage.resolve(Marathon).url)
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

        assertNull(rawStage.resolve(Marathon).url)
        assertEquals("x", stage.name)
        assertEquals("A", stage.resolve(Marathon).url)
        assertEquals("{}", stage.resolve(Marathon).resolveUpgradeStrategy().toString())
    }

    @Test
    public void productionStageIsCreatedUsingNiceDsl() {
        def stageCreator = new MarathonExtension()
        def dsl = {
            globalDefaults {
                marathon {
                    prodUrl = "http://"
                    upgradeStrategy {
                        minimumHealthCapacity = 1
                        maximumOverCapacity = 0
                    }
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

        assertEquals(stage.resolve(Resources).cpus, 1.2, DELTA)
        assertEquals(stage.resolve(Resources).mem, 200, DELTA)
        assertEquals(stage.resolve(Resources).instances, 10)
        assertEquals(stage.resolve(Resources).ports, [0])

        assertEquals(stage.resolve(Marathon).resolveUpgradeStrategy().minimumHealthCapacity, 1, DELTA)
        assertEquals(stage.resolve(Marathon).resolveUpgradeStrategy().maximumOverCapacity, 0, DELTA)
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
        assertEquals(2, testing.resolve(Resources).mem, DELTA)
        assertEquals(10, testing.resolve(Resources).cpus, DELTA)

        def production = stageCreator.getStage("production")
        assertEquals(1, production.resolve(Resources).cpus, DELTA)
        assertEquals(Arrays.asList(0,0), production.resolve(Resources).ports)
    }
}
