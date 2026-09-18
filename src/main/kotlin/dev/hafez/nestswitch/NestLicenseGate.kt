package dev.hafez.nestswitch

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import dev.hafez.nestswitch.license.CheckLicense

/**
 * Single place the paid-feature gate is enforced.
 *
 * [CheckLicense.isLicensed] returns null while the platform's licensing subsystem is still
 * initializing — treated as licensed, so a cold-start never blocks navigation for a paying
 * user. Only an explicit `false` (no license, expired, or a stamp that fails JetBrains
 * certificate verification) closes the gate.
 */
object NestLicenseGate {

    private const val MESSAGE = "Nest Switch requires an active license."
    private const val NOTIFICATION_GROUP = "Nest Switch"

    /** Real check, plus a one-shot notification offering the Register dialog when unlicensed. */
    fun isLicensed(project: Project): Boolean {
        if (CheckLicense.isLicensed() != false) return true
        notifyUnlicensed(project)
        return false
    }

    private fun notifyUnlicensed(project: Project) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP)
            .createNotification(MESSAGE, NotificationType.WARNING)
            .addAction(NotificationAction.createSimple("Activate or start trial") {
                CheckLicense.requestLicense(MESSAGE)
            })
            .notify(project)
    }
}
