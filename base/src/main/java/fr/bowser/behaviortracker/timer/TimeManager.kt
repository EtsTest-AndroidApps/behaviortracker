package fr.bowser.behaviortracker.timer

interface TimeManager {

    fun startTimer(timer: Timer, fakeTimer: Boolean = false)

    fun stopTimer(timer: Timer, fakeTimer: Boolean = false)

    fun updateTime(timer: Timer, newTime: Float, fakeTimer: Boolean = false)

    fun addListener(listener: Listener): Boolean

    fun removeListener(listener: Listener)

    fun stopAllRunningTimers()

    interface Listener {
        fun onTimerStateChanged(updatedTimer: Timer)
        fun onTimerTimeChanged(updatedTimer: Timer)
    }
}