package tmp

import com.wikia.gradle.marathon.AppFactory
import com.wikia.gradle.marathon.common.Stage
import mesosphere.marathon.client.Marathon
import mesosphere.marathon.client.MarathonClient
import mesosphere.marathon.client.model.v2.App
import mesosphere.marathon.client.utils.MarathonException
import org.apache.commons.lang.exception.ExceptionUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import com.wikia.gradle.marathon.common.Marathon as MarathonGradle

fun Project.safeProperty(propertyName: String): Any? {
    if (this.hasProperty(propertyName)) {
        return this.property(propertyName)
    } else {
        return null
    }
}

fun Project.isPropertyTrue(propertyName: String): Boolean {
    return this.safeProperty(propertyName)?.toString()?.toBoolean() ?: false
}

class MarathonTask : DefaultTask() {
    var stage: Stage? = null
    val FORCE_UPDATE = "marathon.forceUpdate"
    val PRESERVE_INSTANCE_ALLOCATION = "marathon.preserveInstanceAllocation"
    val PRESERVE_MEMORY_ALLOCATION = "marathon.preserveMemoryAllocation"
    val PRESERVE_CPU_ALLOCATION = "marathon.preserveCpuAllocation"

    fun prepareAppDescription(): App {
        return AppFactory(this.stage, project).create()
    }

    fun getExistingApp(client: Marathon, appId: String): App? {
        try {
            return client.getApp(appId).app;
        } catch (e: MarathonException) {
            logger.info("exception encountered while fetching data for app", e)
            return null
        }
    }

    fun mergeAppDescriptions(existingApp: App, appDescription: App, project: Project): App {
        if (project.isPropertyTrue(PRESERVE_INSTANCE_ALLOCATION)) {
            appDescription.instances = existingApp.instances;
        }
        if (project.isPropertyTrue(PRESERVE_CPU_ALLOCATION)) {
            appDescription.cpus = existingApp.cpus;
        }
        if (project.isPropertyTrue(PRESERVE_MEMORY_ALLOCATION)) {
            appDescription.mem = existingApp.mem;
        }

        return appDescription
    }

    fun handleUpdateException(e: Exception) {
        /**
         * the root cause of an exception should be a MarathonException, which sadly
         * does not expose its HTTP status directly
         */
        val cause = ExceptionUtils.getRootCause(e)

        when (cause.message?.trim()) {
            "Conflict (http status: 409)" -> {
                val message = "cannot deploy, existing deployment already in progress"
                throw RuntimeException(message)
            }
            else -> throw TaskExecutionException(this, cause)
        }
    }

    @TaskAction
    fun setupApp() {
        this.stage = stage?.validate()

        val url = this.stage?.resolve(MarathonGradle::class.java)?.url ?: throw RuntimeException("marathon url is not defined")
        val marathon = MarathonClient.getInstance(url)

        var appDescription = prepareAppDescription()

        val existingApp = getExistingApp(marathon, appDescription.id)
        if (existingApp != null) {
            appDescription = mergeAppDescriptions(existingApp, appDescription, project)
            try {
                marathon.updateApp(appDescription.id, appDescription, project.isPropertyTrue(FORCE_UPDATE))
            } catch (e: MarathonException) {
                if (e.status == 409) {
                    logger.error("cannot deploy, existing deployment already in progress")
                }
                throw e
            }
        } else {
            marathon.createApp(appDescription)
        }
    }
}
