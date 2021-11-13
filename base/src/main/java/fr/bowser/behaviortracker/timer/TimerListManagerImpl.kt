package fr.bowser.behaviortracker.timer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext

class TimerListManagerImpl(
    private val timerDAO: TimerDAO,
    private val timeManager: TimeManager
) : TimerListManager {

    internal val background = newFixedThreadPoolContext(2, "bg")

    private val timers = ArrayList<Timer>()

    private val listeners = ArrayList<TimerListManager.Listener>()

    init {
        GlobalScope.launch(background) {
            timers.addAll(timerDAO.getTimers())
        }
    }

    override fun addTimer(timer: Timer) {
        timer.position = timers.size

        timers.add(timer)

        for (listener in listeners) {
            listener.onTimerAdded(timer)
        }

        GlobalScope.launch(background) {
            timer.id = timerDAO.addTimer(timer)
        }
    }

    override fun removeTimer(oldTimer: Timer) {
        if (timeManager.getStartedTimer() == oldTimer) {
            timeManager.stopTimer()
        }

        timers.remove(oldTimer)
        for (listener in listeners) {
            listener.onTimerRemoved(oldTimer)
        }

        GlobalScope.launch(background) {
            timerDAO.removeTimer(oldTimer)
            reorderTimerList(timers)
        }
    }

    override fun removeAllTimers() {
        GlobalScope.launch(background) {
            timerDAO.removeAllTimers()
            withContext(Dispatchers.Main) {
                timeManager.stopTimer()
                timers.toList().forEach { oldTimer ->
                    timers.remove(oldTimer)
                    for (listener in listeners) {
                        listener.onTimerRemoved(oldTimer)
                    }
                }
            }
        }
    }

    override fun reorderTimerList(timerList: List<Timer>) {
        for (i in 0 until timerList.size) {
            val timer = timerList[i]
            timer.position = i
        }

        timers.sortBy { timer -> timer.position }

        GlobalScope.launch(background) {
            for (timer in timerList) {
                timerDAO.updateTimerPosition(timer.id, timer.position)
            }
        }
    }

    override fun getTimerList(): List<Timer> {
        return timers
    }

    override fun getTimer(timerId: Long): Timer {
        return timers.first { it.id == timerId }
    }

    override fun renameTimer(timer: Timer, newName: String) {
        timer.name = newName
        GlobalScope.launch(background) {
            timerDAO.renameTimer(timer.id, newName)
        }

        for (listener in listeners) {
            listener.onTimerRenamed(timer)
        }
    }

    override fun addListener(listener: TimerListManager.Listener): Boolean {
        if (listeners.contains(listener)) {
            return false
        }
        return listeners.add(listener)
    }

    override fun removeListener(listener: TimerListManager.Listener): Boolean {
        return listeners.remove(listener)
    }
}