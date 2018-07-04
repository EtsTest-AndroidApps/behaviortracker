package fr.bowser.behaviortracker.event

interface EventManager {

    fun sendTimerCreateEvent(startNow: Boolean)

    fun sendExclusiveTimerModeEvent(isExclusive: Boolean)

    fun sendNewTimeFixTimerDurationEvent(newTimer: Int)

    fun sendSetAlarmEvent(enable: Boolean)

    fun sendDisplayAlarmNotificationEvent()

    fun sendAlarmNotificationClickedEvent()

}