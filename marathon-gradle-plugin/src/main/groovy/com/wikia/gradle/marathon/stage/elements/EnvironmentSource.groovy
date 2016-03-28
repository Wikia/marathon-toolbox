package com.wikia.gradle.marathon.stage.elements

import com.wikia.gradle.marathon.base.Validating

class EnvironmentSource implements Validating {

    List<Closure<String>> consulPrefixes = new LinkedList<>()

    public def fromConsulKV(GString val) {
        consulPrefixes.add { val }
    }

    public def fromConsulKV(Closure closure) {
        consulPrefixes.add(closure)
    }
}
