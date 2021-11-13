package fr.bowser.behaviortracker.timerlist

import dagger.Module
import dagger.Provides
import fr.bowser.behaviortracker.timer.TimeManager
import fr.bowser.behaviortracker.timer.TimerListManager
import fr.bowser.behaviortracker.utils.GenericScope

@Module
class TimerSectionModule(private val screen: TimerSectionContract.Screen) {

    @GenericScope(component = TimerSectionComponent::class)
    @Provides
    fun provideRewardsRowPresenter(
        timerListManager: TimerListManager,
        timeManager: TimeManager
    ): TimerSectionContract.Presenter {
        return TimerSectionPresenter(
            screen,
            timerListManager,
            timeManager
        )
    }
}
