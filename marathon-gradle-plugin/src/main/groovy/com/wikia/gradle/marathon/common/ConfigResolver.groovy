package com.wikia.gradle.marathon.common

class ConfigResolver {

    static <T> Closure<T> dslToParamClosure(Closure<T> closure,
                                            int resolveStrategy = Closure.OWNER_FIRST) {
        return { T param ->
            closure.resolveStrategy = resolveStrategy
            closure.delegate = param
            closure()
            return param
        }
    }

    static <T> T resolveConfig(Closure<T> closure, Class<T> klass) {
        def instance = klass.newInstance()
        if (closure == null) {
            return instance
        } else {
            return closure(instance)
        }
    }

    static <T> T resolveNullConfig(Closure<T> closure, Class<T> klass) {
        if (closure == null) {
            return []
        } else {
            return closure(klass.newInstance())
        }
    }

    static <T> Closure<T> stackClosures(Closure<T> outerClosure, Closure<T> innerClosure) {
        if (outerClosure == null) {
            return innerClosure
        } else if (innerClosure == null) {
            return outerClosure
        } else {
            return { T param ->
                return outerClosure(
                        innerClosure(param)
                )
            }
        }
    }
}
