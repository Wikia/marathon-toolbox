package com.wikia.gradle.marathon.common

class ConfigResolver {
    /**
     * Wraps two closures inside another closure. Supports two type of closures those that modify delegates
     * and those that return data.
     * This enables chaining together two types closures that modify object. That can be evaluated lazily when needed.
     *
     * This seems like it could be solved with simple currying but because we have one closure provided by DSL
     * and another used for chaining them together then curring wont work.
     *
     * @param klass used to discern return type of closure()
     * @param oldClosure
     * @param closure
     */
    static def lazyClosure(Class klass, Closure oldClosure, Closure closure) {
        return {
            def cfg = oldClosure()
            closure.delegate = cfg
            def result = closure()
            if (klass.isInstance(result)) { // closure returned valid result lets use that
                cfg = result
            }
            cfg
        }
    }
}
