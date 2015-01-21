package com.wikia.gradle

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.gradle.api.logging.Logger


class MarathonConnector {
    Logger logger

    def client(marathonURL) { new RESTClient(marathonURL) }

    MarathonConnector(logger = null) {
        this.logger = logger
    }

    def postConfig(String marathonURL, String app_id, String appRequestBody, boolean forceNeedsNewVersion = false) {
        try {
            def currentCfg = this.client(marathonURL).get(path: "/v2/apps/${app_id}") {}
            logger.debug(currentCfg)
        } catch (HttpResponseException ex) {
            if (ex.statusCode != 404) {
                throw ex
            }
        }

        logger.debug("Post to marathon")
        logger.debug(appRequestBody)
        logger.debug(this.client(marathonURL).post(path: "/v2/apps", body: appRequestBody, requestContentType: ContentType.JSON))
    }
}
