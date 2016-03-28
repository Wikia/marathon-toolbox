package com.wikia.gradle.marathon.common

import com.wikia.gradle.marathon.stage.elements.*
import com.wikia.gradle.marathon.stage.elements.marathon.Constraints
import groovy.transform.AutoClone

import static com.wikia.gradle.marathon.common.ConfigResolver.*

@AutoClone
class Stage {

    Map<Class<? extends Validating>, Closure<? extends Validating>> closures = new HashMap<>()

    def resources(Closure dsl) {
        closuresPut(Resources, dsl)
    }

    def environment(Closure dsl) {
        closuresPut(Environment, dsl)
    }

    def environmentSource(Closure dsl) {
        closuresPut(EnvironmentSource, dsl)
    }

    def marathon(Closure dsl) {
        closuresPut(Marathon, dsl)
    }

    def app(Closure dsl) {
        closuresPut(App, dsl)
    }

    def healthchecks(Closure dsl) {
        closuresPut(Healthchecks, dsl)
    }

    def constraints(Closure dsl) {
        closuresPut(Constraints, dsl)
    }

    def closuresPut(Class clazz, Closure dsl) {
        closures.put(clazz, stackClosures(
                dslToParamClosure(dsl), closures.get(clazz)))
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
