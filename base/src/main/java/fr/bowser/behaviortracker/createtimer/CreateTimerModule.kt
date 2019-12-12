package fr.bowser.behaviortracker.createtimer

import android.content.Context
import dagger.Module
import dagger.Provides
import fr.bowser.behaviortracker.event.EventManager
import fr.bowser.behaviortracker.notification.TimeService
import fr.bowser.behaviortracker.pomodoro.PomodoroManager
import fr.bowser.behaviortracker.time.TimeProvider
import fr.bowser.behaviortracker.timer.Timer
import fr.bowser.behaviortracker.timer.TimerListManager
import fr.bowser.behaviortracker.utils.GenericScope

@Module
class CreateTimerModule(private val screen: CreateTimerContract.Screen) {

    @GenericScope(component = CreateTimerComponent::class)
    @Provides
    fun provideCreateTimerPresenter(
        context: Context,
        timerListManager: TimerListManager,
        pomodoroManager: PomodoroManager,
        eventManager: EventManager,
        timeProvider: TimeProvider
    ): CreateTimerContract.Presenter {
        val addOn = createAddOn(context)
        return CreateTimerPresenter(
            screen,
            timerListManager,
            pomodoroManager,
            eventManager,
            timeProvider,
            addOn
        )
    }

    private fun createAddOn(context: Context): CreateTimerPresenter.AddOn {
        return object : CreateTimerPresenter.AddOn {
            override fun startTimer(timer: Timer) {
                TimeService.startTimer(context, timer.id)
            }

        }
    }
}