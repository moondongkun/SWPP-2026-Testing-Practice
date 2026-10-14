package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Tests for [dueDateLabel], one per Tip in specs/DueDateUtils.spec.md.
 */
class DueDateUtilsTest {

    private fun date(year: Int, month: Int, day: Int): LocalDate = LocalDate.of(year, month, day)

    // ---- Success cases ----

    @Test
    fun dueDateLabel_completedWithFutureDueDate_returnsClear() {
        // GIVEN - A completed task due in 6 days
        val dueDate = date(2026, 10, 20)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is CLEAR
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedWithNoDueDate_returnsClear() {
        // GIVEN - A completed task with no due date
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(null, today, isCompleted = true)

        // THEN - The label is CLEAR
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedWithPastDueDate_returnsClear() {
        // GIVEN - A completed task whose due date has passed
        val dueDate = date(2026, 10, 1)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is CLEAR, not D+N
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_activeWithNoDueDate_returnsEmpty() {
        // GIVEN - An active task with no due date
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(null, today, isCompleted = false)

        // THEN - There is no label
        assertEquals("", result)
    }

    @Test
    fun dueDateLabel_activeDueToday_returnsDDay() {
        // GIVEN - An active task due today
        val dueDate = date(2026, 10, 14)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-Day
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_activeDueInFiveDays_returnsDMinus5() {
        // GIVEN - An active task due in 5 days
        val dueDate = date(2026, 10, 19)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-5
        assertEquals("D-5", result)
    }

    @Test
    fun dueDateLabel_activeOverdueByTenDays_returnsDPlus10() {
        // GIVEN - An active task overdue by 10 days
        val dueDate = date(2026, 10, 4)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+10
        assertEquals("D+10", result)
    }

    @Test
    fun dueDateLabel_activeDueInThreeMonths_returnsDMinus92() {
        // GIVEN - An active task due 3 months (92 days) ahead
        val dueDate = date(2027, 1, 14)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label counts total days
        assertEquals("D-92", result)
    }

    @Test
    fun dueDateLabel_activeOverdueByThreeMonths_returnsDPlus92() {
        // GIVEN - An active task overdue by 3 months (92 days)
        val dueDate = date(2026, 7, 14)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label counts total days
        assertEquals("D+92", result)
    }

    // ---- Edge cases: overdue / due today / due later ----

    @Test
    fun dueDateLabel_activeDueYesterday_returnsDPlus1() {
        // GIVEN - An active task due yesterday
        val dueDate = date(2026, 10, 13)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_activeDueTodayAtBoundary_returnsDDay() {
        // GIVEN - An active task due exactly today
        val dueDate = date(2026, 10, 14)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-Day
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_activeDueTomorrow_returnsDMinus1() {
        // GIVEN - An active task due tomorrow
        val dueDate = date(2026, 10, 15)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    // ---- Edge cases: completed vs active at D-Day ----

    @Test
    fun dueDateLabel_completedDueToday_returnsClear() {
        // GIVEN - A completed task due today
        val dueDate = date(2026, 10, 14)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - Completion wins over D-Day
        assertEquals("CLEAR", result)
    }

    // ---- Edge cases: one month ahead ----

    @Test
    fun dueDateLabel_activeDueOneDayBeforeOneMonth_returnsDMinus30() {
        // GIVEN - An active task due one day before the same date next month
        val dueDate = date(2026, 11, 13)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-30
        assertEquals("D-30", result)
    }

    @Test
    fun dueDateLabel_activeDueInExactlyOneMonth_returnsDMinus31() {
        // GIVEN - An active task due on the same date next month
        val dueDate = date(2026, 11, 14)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-31
        assertEquals("D-31", result)
    }

    @Test
    fun dueDateLabel_activeDueOneDayAfterOneMonth_returnsDMinus32() {
        // GIVEN - An active task due one day after the same date next month
        val dueDate = date(2026, 11, 15)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-32
        assertEquals("D-32", result)
    }

    // ---- Edge cases: one month overdue ----

    @Test
    fun dueDateLabel_activeOverdueOneDayLessThanOneMonth_returnsDPlus30() {
        // GIVEN - An active task overdue by one day less than a month
        val dueDate = date(2026, 10, 15)
        val today = date(2026, 11, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+30
        assertEquals("D+30", result)
    }

    @Test
    fun dueDateLabel_activeOverdueByExactlyOneMonth_returnsDPlus31() {
        // GIVEN - An active task overdue by exactly one month
        val dueDate = date(2026, 10, 14)
        val today = date(2026, 11, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+31
        assertEquals("D+31", result)
    }

    @Test
    fun dueDateLabel_activeOverdueOneDayMoreThanOneMonth_returnsDPlus32() {
        // GIVEN - An active task overdue by one day more than a month
        val dueDate = date(2026, 10, 13)
        val today = date(2026, 11, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+32
        assertEquals("D+32", result)
    }

    // ---- Edge cases: month end ----

    @Test
    fun dueDateLabel_activeDueFirstOfNextMonth_returnsDMinus1() {
        // GIVEN - Today is the last day of the month, due the next day
        val dueDate = date(2026, 11, 1)
        val today = date(2026, 10, 31)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueLastOfPreviousMonth_returnsDPlus1() {
        // GIVEN - Today is the first day of the month, due the day before
        val dueDate = date(2026, 10, 31)
        val today = date(2026, 11, 1)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", result)
    }

    // ---- Edge cases: year end ----

    @Test
    fun dueDateLabel_activeDueFirstOfNextYear_returnsDMinus1() {
        // GIVEN - Today is the last day of the year, due the next day
        val dueDate = date(2027, 1, 1)
        val today = date(2026, 12, 31)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueLastOfPreviousYear_returnsDPlus1() {
        // GIVEN - Today is the first day of the year, due the day before
        val dueDate = date(2026, 12, 31)
        val today = date(2027, 1, 1)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", result)
    }

    // ---- Edge cases: one year ahead ----

    @Test
    fun dueDateLabel_activeDueOneDayBeforeOneYear_returnsDMinus364() {
        // GIVEN - An active task due one day before the same date next year
        val dueDate = date(2027, 10, 13)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-364
        assertEquals("D-364", result)
    }

    @Test
    fun dueDateLabel_activeDueInExactlyOneYear_returnsDMinus365() {
        // GIVEN - An active task due on the same date next year
        val dueDate = date(2027, 10, 14)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-365
        assertEquals("D-365", result)
    }

    @Test
    fun dueDateLabel_activeDueOneDayAfterOneYear_returnsDMinus366() {
        // GIVEN - An active task due one day after the same date next year
        val dueDate = date(2027, 10, 15)
        val today = date(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-366
        assertEquals("D-366", result)
    }

    // ---- Edge cases: leap day ----

    @Test
    fun dueDateLabel_activeDueOnLeapDay_returnsDMinus1() {
        // GIVEN - In a leap year, today is Feb 28 and the task is due Feb 29
        val dueDate = date(2028, 2, 29)
        val today = date(2028, 2, 28)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueAfterLeapDay_returnsDMinus2() {
        // GIVEN - In a leap year, today is Feb 28 and the task is due Mar 1
        val dueDate = date(2028, 3, 1)
        val today = date(2028, 2, 28)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-2
        assertEquals("D-2", result)
    }

    @Test
    fun dueDateLabel_activeDueMarchFirstInNonLeapYear_returnsDMinus1() {
        // GIVEN - In a non-leap year, today is Feb 28 and the task is due Mar 1
        val dueDate = date(2027, 3, 1)
        val today = date(2027, 2, 28)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }
}
