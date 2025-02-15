package fr.bowser.behaviortracker.timer_item_view

import fr.bowser.behaviortracker.pomodoro.PomodoroManager
import fr.bowser.behaviortracker.setting.SettingManager
import fr.bowser.behaviortracker.time.TimeProvider
import fr.bowser.behaviortracker.timer.TimeManager
import fr.bowser.behaviortracker.timer.Timer
import fr.bowser.behaviortracker.timer_list.TimerListManager

class TimerItemPresenter(
    private val screen: TimerItemContract.Screen,
    private val timeManager: TimeManager,
    private val timerListManager: TimerListManager,
    private val settingManager: SettingManager,
    private val pomodoroManager: PomodoroManager,
    private val timeProvider: TimeProvider
) : TimerItemContract.Presenter,
    TimerListManager.Listener,
    SettingManager.TimerModificationListener {

    private lateinit var timer: Timer

    private val timeManagerListener = createTimeManagerListener()

    override fun onStart() {
        timerListManager.addListener(this)
        timeManager.addListener(timeManagerListener)

        settingManager.registerTimerModificationListener(this)

        screen.updateTimeModification(settingManager.getTimerModification())
        screen.timerUpdated(timer.time.toLong())
        screen.statusUpdated(timer.isActivate)

        screen.updateLastUpdatedDate(timeProvider.convertTimestampToHumanReadable(timer.lastUpdateTimestamp))
    }

    override fun onStop() {
        settingManager.unregisterTimerModificationListener(this)
        timerListManager.removeListener(this)
        timeManager.removeListener(timeManagerListener)
    }

    override fun setTimer(timer: Timer) {
        this.timer = timer
    }

    override fun onClickCard() {
        screen.startShowMode(timer.id)
    }

    override fun onClickStartPomodoro() {
        pomodoroManager.startPomodoro(timer)
    }

    override fun onClickAddDuration() {
        screen.displayUpdateTimerTimeDialog(timer.id)
    }

    override fun timerStateChange() {
        manageTimerUpdate()

        screen.statusUpdated(timer.isActivate)
    }

    override fun onClickDeleteTimer() {
        timerListManager.removeTimer(timer)
    }

    override fun onClickDecreaseTime() {
        timeManager.updateTime(timer, timer.time - settingManager.getTimerModification())

        screen.timerUpdated(timer.time.toLong())
    }

    override fun onClickIncreaseTime() {
        timeManager.updateTime(timer, timer.time + settingManager.getTimerModification())

        screen.timerUpdated(timer.time.toLong())
    }

    override fun onClickResetTimer() {
        timeManager.updateTime(timer, 0f)

        screen.timerUpdated(timer.time.toLong())
    }

    override fun onClickRenameTimer() {
        screen.displayRenameDialog(timer.name)
    }

    override fun onTimerNameUpdated(newTimerName: String) {
        timerListManager.renameTimer(timer, newTimerName)
    }

    override fun onTimerAdded(updatedTimer: Timer) {
        // nothing to do
    }

    override fun onTimerRemoved(removedTimer: Timer) {
        if (timer == removedTimer) {
            timeManager.removeListener(timeManagerListener)
        }
    }

    override fun onTimerRenamed(updatedTimer: Timer) {
        if (timer == updatedTimer) {
            screen.timerRenamed(updatedTimer.name)
        }
    }

    override fun onTimerModificationChanged(timerModification: Int) {
        screen.updateTimeModification(timerModification)
    }

    private fun manageTimerUpdate() {
        if (!timer.isActivate) {
            timeManager.startTimer(timer)
        } else {
            timeManager.stopTimer()
        }
    }

    private fun createTimeManagerListener() = object : TimeManager.Listener {

        override fun onTimerStateChanged(updatedTimer: Timer) {
            if (timer == updatedTimer) {
                screen.statusUpdated(updatedTimer.isActivate)
                screen.updateLastUpdatedDate(timeProvider.convertTimestampToHumanReadable(timer.lastUpdateTimestamp))
            }
        }

        override fun onTimerTimeChanged(updatedTimer: Timer) {
            if (timer == updatedTimer) {
                screen.timerUpdated(updatedTimer.time.toLong())
            }
        }
    }

    interface AddOn {
        fun startTimer(timer: Timer)
        fun stopTimer(timer: Timer)
    }
}
