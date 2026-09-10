package io.github.airport233.whackamole

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefJSQuery
import javax.swing.JComponent

/**
 * 打地鼠游戏的 JCEF 页面宿主。桥模式与 OCR 插件一致：
 * 页面→宿主单向桥 [JBCefJSQuery]，游戏结束时页面 post "score:N"，宿主弹气球通知。
 */
internal class WhackAMoleWebview(private val project: Project) : Disposable {

    private val browser = JBCefBrowser()
    private val query = JBCefJSQuery.create(browser)

    val component: JComponent get() = browser.component

    init {
        query.addHandler { raw ->
            raw.removePrefix("score:").toIntOrNull()?.let { score ->
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Whack a Mole")
                    .createNotification("Game over — final score: $score", NotificationType.INFORMATION)
                    .notify(project)
            }
            null
        }
        browser.loadHTML(WhackAMoleHtml.page(query.inject("json")))
    }

    override fun dispose() {
        runCatching { query.dispose() }
        runCatching { Disposer.dispose(browser) }
    }
}
