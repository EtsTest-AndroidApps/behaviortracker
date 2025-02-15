package fr.bowser.behaviortracker.pomodoro_view

import dagger.Component
import fr.bowser.behaviortracker.config.BehaviorTrackerAppComponent
import fr.bowser.behaviortracker.utils.GenericScope

@GenericScope(component = PomodoroComponent::class)
@Component(
    modules = [(PomodoroModule::class)],
    dependencies = [(BehaviorTrackerAppComponent::class)]
)
interface PomodoroComponent {

    fun inject(fragment: PomodoroFragment)

    fun providePomodoroPresenter(): PomodoroContract.Presenter
}
