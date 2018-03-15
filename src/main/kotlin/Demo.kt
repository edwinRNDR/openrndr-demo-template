package rndr.studio.demo

import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.FontImageMap
import org.openrndr.math.Matrix44
import studio.rndr.demo.bass.Channel
import studio.rndr.demo.bass.initBass
import studio.rndr.demo.bass.openStream
import studio.rndr.demosystem.Scheduler

/**
 * Demo skeleton
 */
class Demo:Program() {

    lateinit var channel:Channel
    private val scheduler = Scheduler(112.0)

    var showDebug = false

    override fun setup() {
        initBass()
        channel = openStream("data/audio/demo.mp3")

        keyboard.keyDown.filter { it.key == KEY_ARROW_LEFT }.listen {
            channel.setPosition((channel.getPosition() - (60.0 / scheduler.bpm) * 16).coerceAtLeast(0.0))
        }

        keyboard.keyDown.filter { it.key == KEY_ARROW_RIGHT }.listen {
            channel.setPosition(channel.getPosition() + (60.0 / scheduler.bpm) * 16)
        }

        keyboard.keyDown.filter { it.name == "d" }.listen {
            showDebug = !showDebug
        }

        schedule()
        channel.play()
    }

    /**
     * Build the demo's schedule
     */
    fun schedule() {

        val beatDuration = 60.0 / scheduler.bpm
        val barDuration = beatDuration * 4
        val patternDuration = barDuration * 4

        run {
            scheduler.task(patternDuration) {

            }
        }
    }

    override fun draw() {
        drawer.background(ColorRGBa.BLACK)

        val time = channel.getPosition()

        scheduler.update(time)

        if (showDebug) {
            drawer.ortho()
            drawer.view = Matrix44.IDENTITY
            drawer.fontMap =  FontImageMap.fromUrl("file:data/fonts/IBMPlexMono-Bold.ttf", 16.0, window.scale.x)
            drawer.text("position: ${time}", 40.0, 40.0)
        }
    }
}

fun main(args: Array<String>) {
    initBass()
    application(Demo(), configuration {

    })
}