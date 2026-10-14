package com.example.swpp.tododapp.tasks

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.R
import com.example.swpp.tododapp.ServiceLocator
import com.example.swpp.tododapp.data.Task
import com.example.swpp.tododapp.data.source.FakeAndroidTestRepository
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Tests for [setDueDateLabel], one per Tip in specs/TasksListBindings.spec.md.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    private lateinit var repository: FakeAndroidTestRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runTest {
        ServiceLocator.resetRepository()
    }

    /** Creates a new TextView on the main thread. */
    private fun newTextView(): TextView {
        lateinit var textView: TextView
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            textView = TextView(ApplicationProvider.getApplicationContext())
        }
        return textView
    }

    /** Calls setDueDateLabel on the main thread, as data binding does. */
    private fun bind(textView: TextView, task: Task?) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            setDueDateLabel(textView, task)
        }
    }

    // ---- Success cases ----

    @Test
    fun setDueDateLabel_activeDueInThreeDays_showsDMinus3() {
        // GIVEN - A new TextView and an active task due in 3 days
        val textView = newTextView()
        val task = Task("T", "D", false, "id1", LocalDate.now().plusDays(3))

        // WHEN - The label is bound
        bind(textView, task)

        // THEN - D-3 is shown
        assertEquals("D-3", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeDueToday_showsDDay() {
        // GIVEN - A new TextView and an active task due today
        val textView = newTextView()
        val task = Task("T", "D", false, "id1", LocalDate.now())

        // WHEN - The label is bound
        bind(textView, task)

        // THEN - D-Day is shown
        assertEquals("D-Day", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeOverdueByTwoDays_showsDPlus2() {
        // GIVEN - A new TextView and an active task overdue by 2 days
        val textView = newTextView()
        val task = Task("T", "D", false, "id1", LocalDate.now().minusDays(2))

        // WHEN - The label is bound
        bind(textView, task)

        // THEN - D+2 is shown
        assertEquals("D+2", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_completedWithDueDate_showsClear() {
        // GIVEN - A new TextView and a completed task due in 3 days
        val textView = newTextView()
        val task = Task("T", "D", true, "id1", LocalDate.now().plusDays(3))

        // WHEN - The label is bound
        bind(textView, task)

        // THEN - CLEAR is shown
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_completedWithNoDueDate_showsClear() {
        // GIVEN - A new TextView and a completed task with no due date
        val textView = newTextView()
        val task = Task("T", "D", true, "id1", null)

        // WHEN - The label is bound
        bind(textView, task)

        // THEN - CLEAR is shown
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeWithNoDueDate_showsNoLabel() {
        // GIVEN - A new TextView and an active task with no due date
        val textView = newTextView()
        val task = Task("T", "D", false, "id1", null)

        // WHEN - The label is bound
        bind(textView, task)

        // THEN - No label text is shown
        assertEquals("", textView.text.toString())
    }

    @Test
    fun setDueDateLabel_nullTask_showsNoLabel() {
        // GIVEN - A new TextView
        val textView = newTextView()

        // WHEN - The label is bound to a null task
        bind(textView, null)

        // THEN - No label text is shown and nothing is thrown
        assertEquals("", textView.text.toString())
    }

    // ---- Edge cases: re-binding the same view ----

    @Test
    fun setDueDateLabel_uncheckTaskWithNoDueDate_clearsClearLabel() {
        // GIVEN - A TextView showing CLEAR for a completed task with no due date
        val textView = newTextView()
        bind(textView, Task("T", "D", true, "id1", null))
        assertEquals("CLEAR", textView.text.toString())

        // WHEN - The same task is bound again after being unchecked
        bind(textView, Task("T", "D", false, "id1", null))

        // THEN - No label text is shown
        assertEquals("", textView.text.toString())
    }

    @Test
    fun setDueDateLabel_uncheckTaskWithDueDate_showsDMinus3() {
        // GIVEN - A TextView showing CLEAR for a completed task due in 3 days
        val textView = newTextView()
        val dueDate = LocalDate.now().plusDays(3)
        bind(textView, Task("T", "D", true, "id1", dueDate))
        assertEquals("CLEAR", textView.text.toString())

        // WHEN - The same task is bound again after being unchecked
        bind(textView, Task("T", "D", false, "id1", dueDate))

        // THEN - D-3 replaces CLEAR
        assertEquals("D-3", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_checkTaskWithNoDueDate_showsClear() {
        // GIVEN - A TextView bound to an active task with no due date
        val textView = newTextView()
        bind(textView, Task("T", "D", false, "id1", null))
        assertEquals("", textView.text.toString())

        // WHEN - The same task is bound again after being checked
        bind(textView, Task("T", "D", true, "id1", null))

        // THEN - CLEAR is shown
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_recycledRowForTaskWithNoLabel_clearsOldLabel() {
        // GIVEN - A TextView showing D-3 for task A
        val textView = newTextView()
        bind(textView, Task("A", "D", false, "idA", LocalDate.now().plusDays(3)))
        assertEquals("D-3", textView.text.toString())

        // WHEN - The view is reused for task B, which has no due date
        bind(textView, Task("B", "D", false, "idB", null))

        // THEN - Task A's label is gone
        assertEquals("", textView.text.toString())
    }

    // ---- Edge cases: checking and unchecking in the task list ----

    /** Launches TasksFragment with change animations off, so a changed row is re-bound in place. */
    private fun launchTasksFragment() {
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        scenario.onFragment {
            it.requireView().findViewById<RecyclerView>(R.id.tasks_list).itemAnimator = null
        }
    }

    private fun checkboxOfRow(title: String) =
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(title))))

    private fun dueDateLabelOfRow(title: String) =
        onView(allOf(withId(R.id.due_date_text), hasSibling(withText(title))))

    @Test
    fun tasksList_checkThenUncheckTaskWithNoDueDate_showsClearThenNoLabel() = runTest {
        // GIVEN - The task list shows an active task with no due date
        repository.saveTask(Task("TITLE1", "DESC1", false, "id1", null))
        launchTasksFragment()

        // WHEN - The task is checked
        checkboxOfRow("TITLE1").perform(click())

        // THEN - The row shows CLEAR
        dueDateLabelOfRow("TITLE1").check(matches(withText("CLEAR")))

        // WHEN - The task is unchecked
        checkboxOfRow("TITLE1").perform(click())

        // THEN - The row shows no label
        dueDateLabelOfRow("TITLE1").check(matches(withText("")))
    }

    @Test
    fun tasksList_checkThenUncheckTaskWithDueDate_showsClearThenDMinus3() = runTest {
        // GIVEN - The task list shows an active task due in 3 days
        repository.saveTask(Task("TITLE1", "DESC1", false, "id1", LocalDate.now().plusDays(3)))
        launchTasksFragment()

        // WHEN - The task is checked
        checkboxOfRow("TITLE1").perform(click())

        // THEN - The row shows CLEAR
        dueDateLabelOfRow("TITLE1").check(matches(withText("CLEAR")))

        // WHEN - The task is unchecked
        checkboxOfRow("TITLE1").perform(click())

        // THEN - The row shows D-3 again
        dueDateLabelOfRow("TITLE1").check(matches(withText("D-3")))
    }
}
