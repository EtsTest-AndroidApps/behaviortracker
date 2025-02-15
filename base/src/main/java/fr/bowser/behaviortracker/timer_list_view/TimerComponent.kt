package fr.bowser.behaviortracker.timer_list_view

import dagger.Component
import fr.bowser.behaviortracker.config.BehaviorTrackerAppComponent
import fr.bowser.behaviortracker.utils.GenericScope

@GenericScope(component = TimerComponent::class)
@Component(
    modules = [(TimerModule::class)],
    dependencies = [(BehaviorTrackerAppComponent::class)]
)
interface TimerComponent {

    fun inject(fragment: TimerFragment)

    fun provideTimerPresenter(): TimerContract.Presenter
}
