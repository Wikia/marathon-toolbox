package com.wikia.gradle.marathon.stage.elements.marathon

import com.wikia.gradle.marathon.common.Validating

class Constraints implements Validating {
    List<List<String>> constraintList = new LinkedList<>()

    def constraint(String attribute, String operator, String value) {
        List<String> constraints = new ArrayList<>()
        constraints.add(attribute)
        constraints.add(operator)
        constraints.add(value)
        constraintList.add(constraints)
    }

    def constraint(String attribute, String operator) {
        List<String> constraints = new ArrayList<>()
        constraints.add(attribute)
        constraints.add(operator)
        constraintList.add(constraints)
    }

    public def List<List<String>> getConstraints() {
        List<List<String>> ret = new ArrayList<>()

        constraintList.each { list ->
            ret.add(new ArrayList<String>(list))
        }

        return ret
    }
}
