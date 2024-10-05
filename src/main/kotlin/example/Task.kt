package example

import kotlinx.datetime.DayOfWeek

data class ScheduleMoment(val dayOfWeek: DayOfWeek, val time: String)

typealias TaskSchedule = List<ScheduleMoment>

enum class TaskPriority {
    LOW, DEFAULT, HIGH, MAX;
}

data class Task(
    private val name: String,
    val priority: TaskPriority = TaskPriority.DEFAULT,
    private val action: () -> Unit = {},
    private val onFailure: (e: Exception) -> Unit = {},
    private val retries: Int = 0,
    private val schedule: TaskSchedule = emptyList(),
)
