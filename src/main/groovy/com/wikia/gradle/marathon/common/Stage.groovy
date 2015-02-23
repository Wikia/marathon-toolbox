package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone

@AutoClone
class Stage {

    String name = null
    Resources resourcesConfig = new Resources()
    Environment environmentConfig = new Environment()
    MarathonConfig marathonConfig = new MarathonConfig()

    Closure marathonClosure = ConfigResolver.noop()
    Closure environmentClosure = ConfigResolver.noop()
    Closure resourcesClosure = ConfigResolver.noop()

    def resources(Closure closure) {
        this.resourcesClosure = ConfigResolver.stackClosures(
                ConfigResolver.dslToParamClosure(closure), this.resourcesClosure
        )
    }

    def environment(Closure closure) {
        this.environmentClosure = ConfigResolver.stackClosures(
                ConfigResolver.dslToParamClosure(closure), this.environmentClosure
        )
    }

    def marathon(Closure closure) {
        this.marathonClosure = ConfigResolver.stackClosures(
                ConfigResolver.dslToParamClosure(closure),
                this.marathonClosure
        )
    }

    def validate() {
        ["name"].each({ item ->
            if (this.properties.get(item) == null) {
                throw new RuntimeException("Stage.${item} needs to be set")
            }
        })
        this.marathonConfig.validate()
        this.resourcesConfig.validate()
    }

    def resolve(Stage from = null) {
        if (from == null) {
            from = this
        }
        this.marathonConfig = this.getMarathonClosure()(from.marathonConfig)
        this.environmentConfig = this.getEnvironmentClosure()(from.environmentConfig)
        this.resourcesConfig = this.getResourcesClosure()(from.resourcesConfig)
        return this
    }
}
