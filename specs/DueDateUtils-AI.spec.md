# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the D-day label shown next to a task in the task list. A completed task shows `CLEAR`; an active task with no due date shows nothing; an active task with a due date shows `D-N` (due in N days), `D-Day` (due today) or `D+N` (overdue by N days), where N is the total number of calendar days between `today` and `dueDate`.

**Pre-conditions**
- `today` is the current local date of the device (callers pass `LocalDate.now()`).
- `dueDate` is either `null` (no due date) or a valid `LocalDate`.

**Post-conditions**
- Success cases
  - Completed task with a due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-20, today = 2026-10-14, isCompleted = true / When dueDateLabel is called / Then it returns `"CLEAR"`.
  - Completed task with no due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-14, isCompleted = true / When dueDateLabel is called / Then it returns `"CLEAR"`.
  - Completed task whose due date has passed returns `"CLEAR"` (not `D+N`). `[Unit]`
    - Tip: Given dueDate = 2026-10-01, today = 2026-10-14, isCompleted = true / When dueDateLabel is called / Then it returns `"CLEAR"`.
  - Active task with no due date returns `""` (no label). `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `""`.
  - Active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-Day"`.
  - Active task due in N days (same month) returns `"D-N"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-19, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-5"`.
  - Active task overdue by N days (same month) returns `"D+N"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-04, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+10"`.
- Error cases
  - None. Every combination of a nullable `dueDate`, a `today` and a Boolean is valid input. `[Unit]`
- Edge cases
  - Boundary between `D+N` / `D-Day` / `D-N` (dueDate − today = 0). `[Unit]`
    - Tip: Given dueDate = 2026-10-13, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+1"`.
    - Tip: Given dueDate = 2026-10-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-Day"`.
    - Tip: Given dueDate = 2026-10-15, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.
  - Due date in the next month: N counts total days, not the day-of-month difference. `[Unit]`
    - Tip: Given dueDate = 2026-11-01, today = 2026-10-31, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.
    - Tip: Given dueDate = 2026-11-13, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-30"`.
    - Tip: Given dueDate = 2026-11-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-31"`.
    - Tip: Given dueDate = 2026-11-15, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-32"`.
  - Overdue across a month boundary. `[Unit]`
    - Tip: Given dueDate = 2026-10-31, today = 2026-11-01, isCompleted = false / When dueDateLabel is called / Then it returns `"D+1"`.
    - Tip: Given dueDate = 2026-10-14, today = 2026-11-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D+31"`.
  - Due date across a year end. `[Unit]`
    - Tip: Given dueDate = 2027-01-01, today = 2026-12-31, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.
    - Tip: Given dueDate = 2027-10-14, today = 2026-10-14, isCompleted = false / When dueDateLabel is called / Then it returns `"D-365"`.
  - Due date across a leap day. `[Unit]`
    - Tip: Given dueDate = 2028-03-01, today = 2028-02-28, isCompleted = false / When dueDateLabel is called / Then it returns `"D-2"`.
    - Tip: Given dueDate = 2027-03-01, today = 2027-02-28, isCompleted = false / When dueDateLabel is called / Then it returns `"D-1"`.

**Undefined behavior** (optional)
- `today` other than the device's current date: the function is pure, so it still returns a label relative to the given `today`; callers are responsible for passing the right date.
