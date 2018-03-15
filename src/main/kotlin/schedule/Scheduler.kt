package studio.rndr.demosystem

import org.openrndr.math.mod
import java.util.Stack

data class TaskContext(val scheduler: Scheduler, val time: Double, val scheduleTime:Double, val duration: Double, val init: Boolean) {
    /**
     * beat relative to task start time
     */
    val beat get() = time * (scheduler.bpm / 60.0)
    /**
     * bar relative to task start time
     */
    val bar get() = beat / 4.0

    /**
        beat with shift, produces negative beats
     **/
    fun beatShift(beatShift: Double):Double {
        return beat - beatShift
    }

    /**
     * beat offset [0...1.0>
     */
    val beatOffset get() = beat % 1.0
    /**
     * bar offset [0...1.0>
     */
    val barOffset get() = bar % 1.0

    /**
     * bar relative to task start time with a beat shift, produces negative bars
     */
    fun barShift(beatShift: Double): Double = (beat - beatShift) / 4.0

    /**
     * bar offset [0...1.0> with a beat shift
     */
    fun barOffsetShift(beatShift: Double): Double = mod(barShift(beatShift), 1.0)
}

data class Task(val start: Double, val end: Double, val task: (TaskContext) -> Unit)

/**
 * A very simple scheduler which turned out to be surprisingly effective for
 * the programming of techno based visuals.
 */

class Scheduler(val bpm:Double) {

    val tasks = mutableListOf<Task>()

    var activeTasks = setOf<Task>()

    var startCursor = 0.0

    var task: Task? = null

    val cursorStack = Stack<Double>()
    val taskStack = Stack<Task>()


    fun pushCursor() {
        cursorStack.push(startCursor)
    }

    fun popCursor() {
        startCursor = cursorStack.pop()
    }

    fun pushTask() {
        taskStack.push(task)

    }

    fun popTask() {
        task = taskStack.pop()
    }

    fun task(duration: Double, repeat: Int = 1, offset: Double = 0.0, task: (TaskContext) -> Unit) {

        pushCursor()
        for (i in 0 until repeat) {
            tasks.add(Task(startCursor + offset, offset + startCursor + duration, task))
            this.task = tasks.last()

            if (repeat > 1) {
                complete()
            }
        }
        popCursor()
    }

    fun complete() {
        startCursor = task!!.end
    }

    fun update(time: Double) {

        val timeStepTasks = tasks.filter { it.start <= time && time < it.end }

        timeStepTasks.filter { it.start <= time && time < it.end }
                .forEach { it.task(TaskContext(this, time - it.start, time, it.end - it.start, it !in activeTasks)) }

        activeTasks = timeStepTasks.toSet()
    }

    fun delay(time: Double) {
        startCursor += time
    }

}