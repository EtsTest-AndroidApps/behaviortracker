package fr.bowser.behaviortracker.timer_list_view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.bowser.behaviortracker.R
import fr.bowser.behaviortracker.alarm_view.AlarmTimerDialog
import fr.bowser.behaviortracker.config.BehaviorTrackerApp
import fr.bowser.behaviortracker.create_timer_view.CreateTimerDialog
import fr.bowser.behaviortracker.utils.TimeConverter
import javax.inject.Inject

class TimerFragment : Fragment(R.layout.fragment_timer) {

    @Inject
    lateinit var presenter: TimerContract.Presenter

    private val screen = createScreen()

    private lateinit var fab: FloatingActionButton

    private lateinit var emptyListView: ImageView

    private lateinit var emptyListText: TextView

    private lateinit var timerListSectionAdapter: TimerListSectionAdapterDelegate

    private lateinit var timerList: RecyclerView

    private lateinit var totalTimeTv: TextView

    private lateinit var timerListContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        setupGraph()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeList(view)

        timerListContainer = view.findViewById(R.id.timer_list_container_list)
        totalTimeTv = view.findViewById(R.id.timer_list_total_time)

        fab = view.findViewById(R.id.button_add_timer)
        fab.setOnClickListener { presenter.onClickAddTimer() }

        emptyListView = view.findViewById(R.id.empty_list_view)
        emptyListText = view.findViewById(R.id.empty_list_text)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)!!
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        presenter.init()
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!presenter.isInstantApp()) {
            inflater.inflate(R.menu.menu_home, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reset_all -> {
                presenter.onClickResetAll()
                return true
            }
            R.id.menu_remove_all -> {
                presenter.onClickRemoveAllTimers()
                return true
            }
            R.id.menu_settings -> {
                presenter.onClickSettings()
                return true
            }
            R.id.menu_alarm -> {
                presenter.onClickAlarm()
                return true
            }
            R.id.menu_rewards -> {
                presenter.onClickRewards()
                return true
            }
        }
        return false
    }

    private fun createScreen() = object : TimerContract.Screen {
        override fun displayResetAllDialog() {
            val message = resources.getString(R.string.home_dialog_confirm_reset_all_timers)
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(message)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    presenter.onClickResetAllTimers()
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    // do nothing
                }
                .show()
        }

        override fun displaySettingsView() {
            findNavController().navigate(R.id.settings_screen)
        }

        override fun displayAlarmTimerDialog() {
            val alertDialog = AlarmTimerDialog.newInstance()
            alertDialog.show(childFragmentManager, AlarmTimerDialog.TAG)
        }

        override fun displayRewardsView() {
            findNavController().navigate(R.id.rewards_screen)
        }

        override fun displayRemoveAllTimersConfirmationDialog() {
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setTitle(resources.getString(R.string.timer_list_remove_all_timers_title))
                .setMessage(resources.getString(R.string.timer_list_remove_all_timers_message))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    presenter.onClickConfirmRemoveAllTimers()
                }
                .setNegativeButton(android.R.string.cancel, null)
            dialogBuilder.show()
        }

        override fun displayCreateTimerView() {
            CreateTimerDialog.showDialog(activity as AppCompatActivity, false)
        }

        override fun displayTimerListSections(sections: List<TimerListSection>) {
            timerListSectionAdapter.populate(sections)
        }

        override fun displayEmptyListView() {
            timerListContainer.visibility = INVISIBLE
            emptyListView.visibility = VISIBLE
            emptyListText.visibility = VISIBLE

            val fabAnimator = ObjectAnimator.ofFloat(this, PROPERTY_FAB_ANIMATION, 1f, 1.15f, 1f)
            fabAnimator.duration = FAB_ANIMATION_DURATION
            fabAnimator.repeatCount = 1
            fabAnimator.interpolator = AccelerateDecelerateInterpolator()
            fabAnimator.startDelay = FAB_ANIMATION_DELAY
            fabAnimator.start()
        }

        override fun displayListView() {
            timerListContainer.visibility = VISIBLE
            emptyListView.visibility = INVISIBLE
            emptyListText.visibility = INVISIBLE
        }

        override fun updateTotalTime(totalTime: Long) {
            val totalTimeStr = TimeConverter.convertSecondsToHumanTime(totalTime)
            totalTimeTv.text = resources.getString(R.string.timer_list_total_time, totalTimeStr)
        }
    }

    private fun setupGraph() {
        val build = DaggerTimerComponent.builder()
            .behaviorTrackerAppComponent(BehaviorTrackerApp.getAppComponent(requireContext()))
            .timerModule(TimerModule(screen))
            .build()
        build.inject(this)
    }

    private fun initializeList(view: View) {
        timerList = view.findViewById(R.id.list_timers)

        timerListSectionAdapter = TimerListSectionAdapterDelegate()
        timerList.layoutManager = LinearLayoutManager(activity)
        timerList.adapter = timerListSectionAdapter

        timerList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    fab.hide()
                } else {
                    fab.show()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    /**
     * Setter used by {@link #fabAnimator}
     */
    @Keep
    @SuppressWarnings("unused")
    private fun setFabScale(scale: Float) {
        fab.scaleX = scale
        fab.scaleY = scale
    }

    companion object {
        const val TAG = "TimerFragment"

        private const val PROPERTY_FAB_ANIMATION = "fabScale"

        private const val FAB_ANIMATION_DURATION = 1000L
        private const val FAB_ANIMATION_DELAY = 400L
    }
}
