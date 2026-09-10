package io.github.airport233.whackamole

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class WhackAMoleToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // JCEF may not be on the classpath in some IDE versions/runtimes (NoClassDefFoundError).
        val webview = try {
            WhackAMoleWebview(project)
        } catch (e: Throwable) {
            // ProcessCanceledException 是 IntelliJ 取消信号，绝不能吞；其余（含 JCEF 缺失）走占位。
            if (e is ProcessCanceledException) throw e
            thisLogger().warn("[wam] JCEF init failed, falling back to placeholder", e)
            null
        }
        val component = webview?.component ?: placeholder()
        val content = ContentFactory.getInstance().createContent(component, "", false)
        webview?.let { Disposer.register(content, it) }
        toolWindow.contentManager.addContent(content)
    }

    private fun placeholder() = JPanel(BorderLayout()).apply {
        add(JLabel("Whack a Mole requires JCEF, which is unavailable in this IDE.", SwingConstants.CENTER))
    }
}
