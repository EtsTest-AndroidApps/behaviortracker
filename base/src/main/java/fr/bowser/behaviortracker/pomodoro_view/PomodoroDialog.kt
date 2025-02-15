package fr.bowser.behaviortracker.pomodoro_view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fr.bowser.behaviortracker.R
import fr.bowser.behaviortracker.config.BehaviorTrackerApp
import javax.inject.Inject

class PomodoroDialog : DialogFragment() {

    @Inject
    lateinit var presenter: PomodoroDialogContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupGraph()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(resources.getString(R.string.pomodoro_dialog_title))
        dialogBuilder.setMessage(resources.getString(R.string.pomodoro_dialog_content))
        dialogBuilder.setPositiveButton(R.string.pomodoro_dialog_continue) { dialog, which ->
            presenter.onClickPositionButton()
        }
        dialogBuilder.setNegativeButton(R.string.pomodoro_dialog_stop) { dialog, which ->
            presenter.onClickNegativeButton()
        }

        val dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    private fun setupGraph() {
        val component = DaggerPomodoroDialogComponent.builder()
            .behaviorTrackerAppComponent(BehaviorTrackerApp.getAppComponent(requireContext()))
            .pomodoroDialogModule(PomodoroDialogModule())
            .build()
        component.inject(this)
    }

    companion object {

        const val TAG = "PomodoroDialog"

        fun newInstance(): PomodoroDialog {
            return PomodoroDialog()
        }
    }
}
