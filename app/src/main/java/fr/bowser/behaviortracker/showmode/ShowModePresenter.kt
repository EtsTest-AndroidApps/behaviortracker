package fr.bowser.behaviortracker.showmode

import fr.bowser.behaviortracker.timer.TimerListManager

class ShowModePresenter(val view: ShowModeContract.View,
                        val timerListManager: TimerListManager) : ShowModeContract.Presenter {

    private var keepScreeOn = false

    override fun start(selectedTimerId: Long) {
        val timers = timerListManager.getTimerList()

        var selectedTimerPosition = 0
        for (i in 0 until timers.size) {
            val timer = timers[i]
            if (timer.id == selectedTimerId) {
                selectedTimerPosition = i
            }
        }

        view.displayTimerList(timers, selectedTimerPosition)
    }

    override fun onClickScreeOn() {
        keepScreeOn = true
        view.keepScreeOn(true)
    }

    override fun onClickScreeOff() {
        keepScreeOn = false
        view.keepScreeOn(false)
    }

    override fun keepScreenOn(): Boolean {
        return keepScreeOn
    }
}