package io.github.airport233.whackamole

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.Timer

class WhackAMoleToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = WhackAMolePanel()
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        Disposer.register(content, panel)
        toolWindow.contentManager.addContent(content)
    }
}

private const val HOLES = 9
private const val ROUND_SECONDS = 30
private const val MOLE_INTERVAL_MS = 700
private const val MOLE = "🐹"
private const val EMPTY = "  "

private class WhackAMolePanel : JPanel(BorderLayout(8, 8)), Disposable {
    private val holes = List(HOLES) { JButton(EMPTY) }
    private var moleAt = -1
    private var score = 0
    private var timeLeft = ROUND_SECONDS

    private val scoreLabel = JLabel("Score: 0", SwingConstants.CENTER)
    private val timeLabel = JLabel("Time: $ROUND_SECONDS", SwingConstants.CENTER)
    private val startButton = JButton("Start")

    private val moleTimer = Timer(MOLE_INTERVAL_MS) { moveMole() }
    private val roundTimer = Timer(1000) { tick() }

    init {
        val grid = JPanel(GridLayout(3, 3, 4, 4))
        holes.forEachIndexed { index, button ->
            button.isEnabled = false
            button.addActionListener { whack(index) }
            grid.add(button)
        }
        val top = JPanel(GridLayout(1, 3, 4, 4))
        top.add(scoreLabel)
        top.add(timeLabel)
        top.add(startButton)
        add(top, BorderLayout.NORTH)
        add(grid, BorderLayout.CENTER)

        startButton.addActionListener { startRound() }
    }

    private fun startRound() {
        score = 0
        timeLeft = ROUND_SECONDS
        moleAt = -1
        holes.forEach {
            it.isEnabled = true
            it.text = EMPTY
        }
        startButton.isEnabled = false
        updateLabels()
        moveMole()
        moleTimer.start()
        roundTimer.start()
    }

    private fun moveMole() {
        if (moleAt >= 0) holes[moleAt].text = EMPTY
        var next = (0 until HOLES).random()
        if (next == moleAt) next = (next + 1) % HOLES
        moleAt = next
        holes[next].text = MOLE
    }

    private fun whack(index: Int) {
        if (index != moleAt) return
        score++
        holes[moleAt].text = EMPTY
        moleAt = -1
        moveMole()
        updateLabels()
    }

    private fun tick() {
        timeLeft--
        if (timeLeft <= 0) {
            endRound()
            return
        }
        updateLabels()
    }

    private fun endRound() {
        moleTimer.stop()
        roundTimer.stop()
        holes.forEach {
            it.isEnabled = false
            it.text = EMPTY
        }
        moleAt = -1
        timeLabel.text = "Final: $score"
        startButton.text = "Play again"
        startButton.isEnabled = true
    }

    private fun updateLabels() {
        scoreLabel.text = "Score: $score"
        timeLabel.text = "Time: $timeLeft"
    }

    override fun dispose() {
        moleTimer.stop()
        roundTimer.stop()
    }
}
