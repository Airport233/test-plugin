package io.github.airport233.testplugin

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class HelloAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Test Plugin")
            .createNotification("Test Plugin is alive", NotificationType.INFORMATION)
            .notify(e.project)
    }
}
