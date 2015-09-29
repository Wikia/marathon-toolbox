package com.wikia.gradle.marathon.common
import groovy.transform.AutoClone
import mesosphere.marathon.client.model.v2.UpgradeStrategy

@AutoClone
class Marathon implements Validating {

    String prodUrl
    String devUrl
    Boolean useProd
    Closure upgradeStrategy

    def validate() {
        if (this.properties.get("prodUrl") == null) {
            throw new RuntimeException("Marathon.devUrl needs to be set")
        }

        if (this.properties.get("devUrl") == null) {
            throw new RuntimeException("Marathon.prodUrl needs to be set")
        }
    }

    def upgradeStrategy(Closure upgradeStrategy) {
        this.upgradeStrategy = upgradeStrategy
    }

    def resolveUpgradeStrategy() {
        UpgradeStrategy realUpgradeStrategy = new UpgradeStrategy()
        this.upgradeStrategy.delegate = realUpgradeStrategy
        this.upgradeStrategy.run()
        return realUpgradeStrategy
    }

    def getUrl() {
        if (useProd) {
            return prodUrl
        } else {
            return devUrl
        }
    }
}
