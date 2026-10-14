# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the D-day label shown next to a task in the task list. A completed task shows `CLEAR` with or without a due date; an active task with no due date shows no label; an active task with a due date shows `D-N` (due in N days), `D-Day` (due today) or `D+N` (overdue by N days), where N is the total number of calendar days between `today` and `dueDate`.

**Pre-conditions**
- None. The function is pure: any `today`, any nullable `dueDate` and either `isCompleted` value may be passed.

**Post-conditions**
- Success cases
  - Completed task with a future due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-20, today = 2026-10-14, isCompleted = true / When dueDateLabel is called / Then it returns `"CLEAR"`.
  - Completed task with no due date (optional value left empty) returns `"CLEAR"`. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-14, isCompleted = true / When dueDateLabel is called / Then it returns `"CLEAR"`.
  - Completed task whose due date has passed returns `"CLEAR"`, not `D+N`. `[Unit]`
    - Tip: Given dueDate = 2026-10-01, today = 2026-10-14, isCompleted = true / When dueDateLabel is called / Then it returns `"CLEAR"`.
  - Active task with no due date (optional value left empty) returns `""` (no label). `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `""`.
  - Active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-Day"`.
  - Active task due in N days returns `"D-N"`: N in plain decimal, no leading zeros, no spaces. `[Unit]`
    - Tip: Given dueDate = 2026-10-19, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-5"`.
  - Active task overdue by N days returns `"D+N"`: N in plain decimal, no leading zeros, no spaces. `[Unit]`
    - Tip: Given dueDate = 2026-10-04, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+10"`.
  - Active task due several months ahead returns the total day count, not the day-of-month difference. `[Unit]`
    - Tip: Given dueDate = 2027-01-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-92"`.
  - Active task overdue by several months returns the total day count. `[Unit]`
    - Tip: Given dueDate = 2026-07-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+92"`.
- Error cases
  - None. Every combination of a nullable `dueDate`, a `today` and a Boolean is valid input.
- Edge cases
  - Boundary between overdue / due today / due later (dueDate − today = 0). `[Unit]`
    - Tip: Given dueDate = 2026-10-13, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+1"`.
    - Tip: Given dueDate = 2026-10-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-Day"`.
    - Tip: Given dueDate = 2026-10-15, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.
  - Boundary between completed and active at the D-Day point: completion wins. `[Unit]`
    - Tip: Given dueDate = 2026-10-14, today = 2026-10-14, isCompleted = true / When dueDateLabel is called / Then it returns `"CLEAR"`.
  - Boundary of one month ahead (2026-10-14 + 1 month = 2026-11-14). `[Unit]`
    - Tip: Given dueDate = 2026-11-13, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-30"`.
    - Tip: Given dueDate = 2026-11-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-31"`.
    - Tip: Given dueDate = 2026-11-15, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-32"`.
  - Boundary of one month overdue (2026-11-14 − 1 month = 2026-10-14). `[Unit]`
    - Tip: Given dueDate = 2026-10-15, today = 2026-11-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+30"`.
    - Tip: Given dueDate = 2026-10-14, today = 2026-11-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+31"`.
    - Tip: Given dueDate = 2026-10-13, today = 2026-11-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+32"`.
  - Month end: the next/previous day lies in another month. `[Unit]`
    - Tip: Given dueDate = 2026-11-01, today = 2026-10-31, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.
    - Tip: Given dueDate = 2026-10-31, today = 2026-11-01, isCompleted = false / When dueDateLabel is called / Then it returns `"D+1"`.
  - Year end: the next/previous day lies in another year. `[Unit]`
    - Tip: Given dueDate = 2027-01-01, today = 2026-12-31, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.
    - Tip: Given dueDate = 2026-12-31, today = 2027-01-01, isCompleted = false / When dueDateLabel is called / Then it returns `"D+1"`.
  - Boundary of one year ahead (2026-10-14 + 1 year = 2027-10-14). `[Unit]`
    - Tip: Given dueDate = 2027-10-13, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-364"`.
    - Tip: Given dueDate = 2027-10-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-365"`.
    - Tip: Given dueDate = 2027-10-15, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-366"`.
  - Leap day (2028 is a leap year, 2027 is not). `[Unit]`
    - Tip: Given dueDate = 2028-02-29, today = 2028-02-28, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.
    - Tip: Given dueDate = 2028-03-01, today = 2028-02-28, isCompleted = false / When dueDateLabel is called / Then it returns `"D-2"`.
    - Tip: Given dueDate = 2027-03-01, today = 2027-02-28, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.
