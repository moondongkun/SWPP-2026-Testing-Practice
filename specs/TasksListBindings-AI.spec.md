# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `@BindingAdapter("app:dueDateLabel") fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Show the D-day label of `task` (as defined by `dueDateLabel(task.dueDate, today, task.isCompleted)`) in the task row's `due_date_text` view. When the task has no label, nothing is shown. Because list rows are re-bound (RecyclerView recycling, checking/unchecking the completed box), every call must fully replace what a previous call displayed.

**Pre-conditions**
- Called on the main thread with a `TextView` (the row's `due_date_text`).
- `today` is the device's current date (`LocalDate.now()`).

**Post-conditions**
- Success cases
  - Active task due in N days: text is `"D-N"` and the view is `VISIBLE`. `[Integration]`
    - Tip: Given a TextView and Task("T", "D", false, "id1", dueDate = LocalDate.now().plusDays(3)) / When setDueDateLabel is called / Then text is `"D-3"` and visibility is `View.VISIBLE`.
  - Active task due today: text is `"D-Day"` and the view is `VISIBLE`. `[Integration]`
    - Tip: Given a TextView and Task("T", "D", false, "id1", dueDate = LocalDate.now()) / When setDueDateLabel is called / Then text is `"D-Day"` and visibility is `View.VISIBLE`.
  - Active overdue task: text is `"D+N"` and the view is `VISIBLE`. `[Integration]`
    - Tip: Given a TextView and Task("T", "D", false, "id1", dueDate = LocalDate.now().minusDays(2)) / When setDueDateLabel is called / Then text is `"D+2"` and visibility is `View.VISIBLE`.
  - Completed task with a due date: text is `"CLEAR"` and the view is `VISIBLE`. `[Integration]`
    - Tip: Given a TextView and Task("T", "D", true, "id1", dueDate = LocalDate.now().plusDays(3)) / When setDueDateLabel is called / Then text is `"CLEAR"` and visibility is `View.VISIBLE`.
  - Completed task with no due date: text is `"CLEAR"` and the view is `VISIBLE`. `[Integration]`
    - Tip: Given a TextView and Task("T", "D", true, "id1", dueDate = null) / When setDueDateLabel is called / Then text is `"CLEAR"` and visibility is `View.VISIBLE`.
  - Active task with no due date: no label — text is `""` and the view is `GONE`. `[Integration]`
    - Tip: Given a TextView and Task("T", "D", false, "id1", dueDate = null) / When setDueDateLabel is called / Then text is `""` and visibility is `View.GONE`.
- Error cases
  - `task == null` (binding not yet set): no label — text is `""` and the view is `GONE`; no exception. `[Integration]`
    - Tip: Given a TextView / When setDueDateLabel(textView, null) is called / Then text is `""` and visibility is `View.GONE`.
- Edge cases
  - Re-binding from a labeled task to an unlabeled one clears the old label (completed → unchecked, no due date). `[Integration]`
    - Tip: Given a TextView already bound to Task("T", "D", true, "id1", dueDate = null) showing `"CLEAR"` / When setDueDateLabel is called again with Task("T", "D", false, "id1", dueDate = null) / Then text is `""` and visibility is `View.GONE`.
  - Re-binding from an unlabeled task to a labeled one shows the new label. `[Integration]`
    - Tip: Given a TextView already bound to Task("T", "D", false, "id1", dueDate = null) / When setDueDateLabel is called again with Task("T", "D", true, "id1", dueDate = null) / Then text is `"CLEAR"` and visibility is `View.VISIBLE`.
  - Re-binding from a completed task with a due date to the same task unchecked replaces `CLEAR` with the D-day label. `[Integration]`
    - Tip: Given a TextView already bound to Task("T", "D", true, "id1", dueDate = LocalDate.now().plusDays(3)) showing `"CLEAR"` / When setDueDateLabel is called again with Task("T", "D", false, "id1", dueDate = LocalDate.now().plusDays(3)) / Then text is `"D-3"` and visibility is `View.VISIBLE`.
  - In the task list, checking and then unchecking a task with no due date leaves no label. `[Integration]`
    - Tip: Given FakeAndroidTestRepository holds Task("TITLE1", "DESC1", false, "id1", dueDate = null) and TasksFragment is launched / When the row's complete checkbox is clicked twice / Then no view with text `"CLEAR"` is displayed in the row.

**Undefined behavior** (optional)
- The exact day-count rules (month/year/leap boundaries) belong to `dueDateLabel` and are specified in `DueDateUtils.spec.md`; this spec only checks that its result is shown or hidden correctly.
