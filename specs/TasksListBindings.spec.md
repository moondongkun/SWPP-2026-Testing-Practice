# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `@BindingAdapter("app:dueDateLabel") fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Show the D-day label of `task` for today's date in the task row's `due_date_text` view, as defined by `dueDateLabel(task.dueDate, LocalDate.now(), task.isCompleted)` in `DueDateUtils.spec.md`. When the task has no label, no label text is shown. List rows are bound again whenever the task changes (checking/unchecking it) or the row is recycled, so every call must fully replace what an earlier call displayed.

**Pre-conditions**
- Called on the main thread with the row's `due_date_text` `TextView`.
- The test does not run across local midnight (the function reads `LocalDate.now()` itself, so expected labels are computed from `LocalDate.now()` in the test).

**Post-conditions**
- Success cases
  - Active task due in N days: text is `"D-N"` and the view is visible. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, "id1", LocalDate.now().plusDays(3)) / When setDueDateLabel is called / Then text is `"D-3"` and visibility is `View.VISIBLE`.
  - Active task due today: text is `"D-Day"` and the view is visible. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, "id1", LocalDate.now()) / When setDueDateLabel is called / Then text is `"D-Day"` and visibility is `View.VISIBLE`.
  - Active overdue task: text is `"D+N"` and the view is visible. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, "id1", LocalDate.now().minusDays(2)) / When setDueDateLabel is called / Then text is `"D+2"` and visibility is `View.VISIBLE`.
  - Completed task with a due date: text is `"CLEAR"` and the view is visible. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", true, "id1", LocalDate.now().plusDays(3)) / When setDueDateLabel is called / Then text is `"CLEAR"` and visibility is `View.VISIBLE`.
  - Completed task with no due date: text is `"CLEAR"` and the view is visible. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", true, "id1", null) / When setDueDateLabel is called / Then text is `"CLEAR"` and visibility is `View.VISIBLE`.
  - Active task with no due date (optional value left empty): no label, so text is `""`. `[Integration]`
    - Tip: Given a new TextView and Task("T", "D", false, "id1", null) / When setDueDateLabel is called / Then text is `""`.
  - `task == null` (binding variable not set yet): no label, so text is `""`, and no exception is thrown. `[Integration]`
    - Tip: Given a new TextView / When setDueDateLabel(textView, null) is called / Then text is `""`.
- Error cases
  - None. A null `task` is a valid "not bound yet" input (see Success cases).
- Edge cases
  - Same task bound again after unchecking, no due date: `"CLEAR"` → no label. `[Integration]`
    - Tip: Given a new TextView bound to Task("T", "D", true, "id1", null) so text is `"CLEAR"` / When setDueDateLabel is called again with Task("T", "D", false, "id1", null) / Then text is `""`.
  - Same task bound again after unchecking, with a due date: `"CLEAR"` → `"D-N"`. `[Integration]`
    - Tip: Given a new TextView bound to Task("T", "D", true, "id1", LocalDate.now().plusDays(3)) so text is `"CLEAR"` / When setDueDateLabel is called again with Task("T", "D", false, "id1", LocalDate.now().plusDays(3)) / Then text is `"D-3"` and visibility is `View.VISIBLE`.
  - Same task bound again after checking, no due date: no label → `"CLEAR"`. `[Integration]`
    - Tip: Given a new TextView bound to Task("T", "D", false, "id1", null) so text is `""` / When setDueDateLabel is called again with Task("T", "D", true, "id1", null) / Then text is `"CLEAR"` and visibility is `View.VISIBLE`.
  - Recycled row: a view that showed another task's label is bound to a task with no label. `[Integration]`
    - Tip: Given a new TextView bound to Task("A", "D", false, "idA", LocalDate.now().plusDays(3)) so text is `"D-3"` / When setDueDateLabel is called again with Task("B", "D", false, "idB", null) / Then text is `""`.
  - In the task list, checking then unchecking a task with no due date shows `"CLEAR"`, then no label. `[Integration]`
    - Tip: Given FakeAndroidTestRepository holds Task("TITLE1", "DESC1", false, "id1", null) and TasksFragment is launched with R.style.AppTheme / When the row's `complete_checkbox` is clicked once, then clicked again / Then after the first click the row's `due_date_text` (sibling of the `title_text` with text `"TITLE1"`) has text `"CLEAR"`, and after the second click it has text `""`.
  - In the task list, checking then unchecking a task with a due date shows `"CLEAR"`, then `"D-N"` again. `[Integration]`
    - Tip: Given FakeAndroidTestRepository holds Task("TITLE1", "DESC1", false, "id1", LocalDate.now().plusDays(3)) and TasksFragment is launched with R.style.AppTheme / When the row's `complete_checkbox` is clicked once, then clicked again / Then after the first click the row's `due_date_text` has text `"CLEAR"`, and after the second click it has text `"D-3"`.

**Undefined behavior** (optional)
- Visibility of the view when there is no label (`VISIBLE` with empty text, `INVISIBLE` or `GONE`): the intent only says no label is shown, and an empty text shows nothing in every case, so tests check the text only.
- Day-count rules across month/year/leap boundaries: specified and tested in `DueDateUtils.spec.md`, not repeated here.
