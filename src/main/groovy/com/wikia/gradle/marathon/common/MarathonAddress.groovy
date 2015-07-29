package com.wikia.gradle.marathon.common

import groovy.transform.AutoClone

@AutoClone
class MarathonAddress implements Validating {

    String prodUrl
    String devUrl
    Boolean useProd

    def validate() {
        if (this.properties.get("prodUrl") == null) {
            throw new RuntimeException("Marathon.devUrl needs to be set")
        }

        if (this.properties.get("devUrl") == null) {
            throw new RuntimeException("Marathon.prodUrl needs to be set")
        }
    }

    def getUrl() {
        if (useProd) {
            return prodUrl
        } else {
            return devUrl
        }
    }
}
