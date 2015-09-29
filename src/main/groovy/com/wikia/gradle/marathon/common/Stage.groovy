package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone

import static com.wikia.gradle.marathon.common.ConfigResolver.*

@AutoClone
class Stage {

    String name
    Map<Class<? extends Validating>, Closure<? extends Validating>> closures = new HashMap<>()

    def resources(Closure dsl) {
        closures.put(Resources, stackClosures(
                dslToParamClosure(dsl), closures.get(Resources)
        ))
    }

    def environment(Closure dsl) {
        closures.put(Environment, stackClosures(
                dslToParamClosure(dsl), closures.get(Environment)
        ))
    }

    def marathon(Closure dsl) {
        closures.put(Marathon, stackClosures(
                dslToParamClosure(dsl), closures.get(Marathon)
        ))
    }

    def app(Closure dsl) {
        closures.put(App, stackClosures(
                dslToParamClosure(dsl), closures.get(App)
        ))
    }

    def healthchecks(Closure dsl) {
        closures.put(Healthchecks, stackClosures(
                dslToParamClosure(dsl), closures.get(Healthchecks)
        ))
    }

    def validate() {
        if (this.name == null) {
            throw new RuntimeException("Stage.name needs to be set")
        }
        for (Class<? extends Validating> klass in this.closures.keySet()) {
            this.resolve(klass).validate()
        }
        return this
    }

    def insertBefore(Stage from) {
        for (Class clazz in from.closures.keySet()) {
            this.closures.
                    put(clazz, stackClosures(this.closures.get(clazz), from.closures.get(clazz)))
        }
        return this
    }

    public <T> T resolve(Class<T> clazz) {
        return resolveConfig(this.closures.get(clazz), clazz as Class<? extends Validating>) as T
    }
}
