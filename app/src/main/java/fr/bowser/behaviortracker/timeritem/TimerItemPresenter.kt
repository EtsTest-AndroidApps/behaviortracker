package fr.bowser.behaviortracker.timeritem

import fr.bowser.behaviortracker.notification.TimerNotificationManager
import fr.bowser.behaviortracker.setting.SettingManager
import fr.bowser.behaviortracker.timer.TimeManager
import fr.bowser.behaviortracker.timer.Timer
import fr.bowser.behaviortracker.timer.TimerListManager

class TimerItemPresenter(private val view: TimerItemContract.View,
                         private val timeManager: TimeManager,
                         private val timerListManager: TimerListManager,
                         private val timerNotificationManager: TimerNotificationManager,
                         private val settingManager: SettingManager)
    : TimerItemContract.Presenter,
        TimerListManager.TimerCallback {

    private lateinit var timer: Timer

    override fun start() {
        timerListManager.registerTimerCallback(this)
        timeManager.registerUpdateTimerCallback(updateTimerCallback)

        view.updateTimeModification(settingManager.getTimerModification())
        view.timerUpdated(timer.time.toLong())
        view.statusUpdated(timer.isActivate)
    }

    override fun stop() {
        timerListManager.unregisterTimerCallback(this)
        timeManager.unregisterUpdateTimerCallback(updateTimerCallback)
    }

    override fun setTimer(timer: Timer) {
        this.timer = timer
    }

    override fun onClickCard() {
        view.startShowMode(timer.id)
    }

    override fun timerStateChange() {
        manageTimerUpdate()

        view.statusUpdated(timer.isActivate)
    }

    override fun onClickDeleteTimer() {
        timerListManager.removeTimer(timer)
    }

    override fun onClickDecreaseTime() {
        timeManager.updateTime(timer, timer.time - settingManager.getTimerModification())

        view.timerUpdated(timer.time.toLong())
    }

    override fun onClickIncreaseTime() {
        timeManager.updateTime(timer, timer.time + settingManager.getTimerModification())

        view.timerUpdated(timer.time.toLong())
    }

    override fun onClickResetTimer() {
        timeManager.updateTime(timer, 0f)

        view.timerUpdated(timer.time.toLong())
    }

    override fun onClickRenameTimer() {
        view.displayRenameDialog(timer.name)
    }

    override fun onTimerNameUpdated(newTimerName: String) {
        timerListManager.renameTimer(timer, newTimerName)
    }

    override fun onTimerAdded(updatedTimer: Timer) {
        // nothing to do
    }

    override fun onTimerRemoved(updatedTimer: Timer) {
        if (timer == updatedTimer) {
            timeManager.unregisterUpdateTimerCallback(updateTimerCallback)
        }
    }

    override fun onTimerRenamed(updatedTimer: Timer) {
        if (timer == updatedTimer) {
            view.timerRenamed(updatedTimer.name)
        }
    }

    private fun manageTimerUpdate() {
        if (!timer.isActivate) {
            timeManager.startTimer(timer)
        } else {
            timeManager.stopTimer(timer)
        }
    }

    private val updateTimerCallback =
            object : TimeManager.TimerCallback {

                override fun onTimerStateChanged(updatedTimer: Timer) {
                    if (timer == updatedTimer) {
                        view.statusUpdated(updatedTimer.isActivate)
                    }
                }

                override fun onTimerTimeChanged(updatedTimer: Timer) {
                    if (timer == updatedTimer) {
                        view.timerUpdated(updatedTimer.time.toLong())
                    }
                }
            }

}