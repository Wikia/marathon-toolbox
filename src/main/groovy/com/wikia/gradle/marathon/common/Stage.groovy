package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone

@AutoClone
class Stage {

    String name = null
    Resources resourcesConfig = new Resources()
    Environment environmentConfig = new Environment()
    MarathonConfig marathonConfig = new MarathonConfig()

    Closure marathonClosure = {
        this.marathonConfig
    }
    Closure environmentClosure = {
        this.environmentConfig
    }
    Closure resourcesClosure = {
        this.resourcesConfig
    }

    def resources(Closure closure) {
        this.resourcesClosure = ConfigResolver.lazyClosure(Resources.class, this.resourcesClosure, closure)
        return this.resourcesClosure
    }

    def environment(Closure closure) {
        this.environmentClosure = ConfigResolver.lazyClosure(Environment.class, this.environmentClosure, closure)
        return this.environmentClosure
    }

    def marathon(Closure closure) {
        this.marathonClosure = ConfigResolver.lazyClosure(MarathonConfig.class, this.marathonClosure, closure)
        return this.marathonClosure
    }

    def validate() {
        ["name"].forEach({ item ->
            if (this.properties.get(item) == null) {
                throw new RuntimeException("Stage.${item} needs to be set")
            }
        })
        this.marathonConfig.validate()
        this.resourcesConfig.validate()
    }

    def resolve() {
        this.environmentConfig = this.environmentClosure()
        this.resourcesConfig = this.resourcesClosure()
        this.marathonConfig = this.marathonClosure()
        return this
    }
}
