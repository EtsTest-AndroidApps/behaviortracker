package fr.bowser.behaviortracker.pomodoro

import fr.bowser.behaviortracker.timer.TimeManager
import fr.bowser.behaviortracker.timer.Timer

class PomodoroManagerImpl(private val timeManager: TimeManager) : PomodoroManager{

    override var listener: PomodoroManager.Listener? = null

    override var pomodoroTime = 0L

    override var currentTimer: Timer? = null

    var actionTimer: Timer? = null
        private set
    var restTimer: Timer? = null
        private set

    var isRunning = false
        private set
    var isStarted = false
        private set

    private var actionDuration = 0L
    private var restDuration = 0L

    override fun startPomodoro(actionTimer: Timer,
                      actionDuration: Long,
                      restTimer: Timer,
                      restDuration: Long) {
        this.actionTimer = actionTimer
        this.restTimer = restTimer
        this.actionDuration = actionDuration
        this.restDuration = restDuration

        currentTimer = actionTimer
        pomodoroTime = actionDuration

        isStarted = true
        isRunning = true

        timeManager.registerUpdateTimerCallback(timeManagerCallback)
        timeManager.startTimer(currentTimer!!)

        listener?.onCountFinished(currentTimer!!, pomodoroTime)
    }

    override fun resume() {
        if (!isStarted) {
            return
        }
        isRunning = true
        timeManager.startTimer(currentTimer!!)
    }

    override fun pause() {
        if (!isStarted) {
            return
        }
        isRunning = false
        timeManager.stopTimer(currentTimer!!)
    }

    override fun stop() {
        isStarted = false
        actionTimer = null
        restTimer = null
        timeManager.unregisterUpdateTimerCallback(timeManagerCallback)
    }

    private val timeManagerCallback = object : TimeManager.TimerCallback {

        override fun onTimerStateChanged(updatedTimer: Timer) {
            if (actionTimer == updatedTimer || updatedTimer == restTimer) {
                listener?.onTimerStateChanged(updatedTimer)
            }
        }

        override fun onTimerTimeChanged(updatedTimer: Timer) {
            if (updatedTimer != currentTimer) {
                return
            }

            pomodoroTime--

            listener?.updateTime(currentTimer!!, pomodoroTime)

            if (pomodoroTime > 0L) {
                return
            }

            val previousTimer: Timer
            if (currentTimer == actionTimer) {
                currentTimer = restTimer
                previousTimer = actionTimer!!
                pomodoroTime = restDuration
            } else {
                currentTimer = actionTimer
                previousTimer = restTimer!!
                pomodoroTime = actionDuration
            }

            timeManager.startTimer(currentTimer!!)
            timeManager.stopTimer(previousTimer)

            listener?.onCountFinished(currentTimer!!, pomodoroTime)
        }

    }

}