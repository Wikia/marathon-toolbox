package com.wikia.gradle.marathon
//
//import com.wikia.gradle.marathon.common.ConfigResolver
//import com.wikia.gradle.marathon.common.Validating
//import groovy.lang.Closure
//import groovy.transform.AutoClone
//import mesosphere.marathon.client.model.v2.UpgradeStrategy
//
//
//@AutoClone
//class MarathonTmp : Validating {
//
//    var marathonUrl: String? = null
//    var rawUpgradeStrategy: Closure<UpgradeStrategy>? = null
//
//    var rawLabels: Closure<Any>? = null
//    var rawConstraints: Closure<Any>? = null
//    var backoffFactor: Double? = null
//    var backoffSeconds: Integer? = null
//    var maxLaunchDelaySeconds: Integer? = null
//    var id: String? = null
//
//
//    override fun validate() {
//        if (this.marathonUrl == null) {
//            throw RuntimeException("Marathon.marathonUrl needs to be set")
//        }
//    }
//
//    fun upgradeStrategy(upgradeStrategy: Closure<Any>) {
//        setUpgradeStrategy(upgradeStrategy)
//    }
//
//    fun setUpgradeStrategy(upgradeStrategy: Closure<Any>) {
//        this.rawUpgradeStrategy = ConfigResolver.dslToParamClosure<Any>(upgradeStrategy)
//    }
//
//    fun resolveUpgradeStrategy(): UpgradeStrategy {
//        return ConfigResolver.resolveNullConfig(this.rawUpgradeStrategy, UpgradeStrategy::class.java)
//    }
//
//    fun labels(labels: Closure<Any>) {
//        setLabels(labels)
//    }
//
//    fun setLabels(labels: Closure<Any>) {
//        this.rawLabels = ConfigResolver.dslToParamClosure<Any>(labels)
//    }
//
////    Labels resolveLabels()
////    {
////        return resolveNullConfig(this.rawLabels, Labels)
////    }
////
////    def constraints(Closure constraints)
////    {
////        setConstraints(constraints)
////    }
////
////    def setConstraints(Closure constraints)
////    {
////        this.rawConstraints = dslToParamClosure(constraints)
////    }
////
////    Constraints resolveConstraints()
////    {
////        return resolveNullConfig(this.rawConstraints, Constraints)
////    }
////
////    def getUrl()
////    {
////        return marathonUrl
////    }
//}
