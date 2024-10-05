package example

import kotlinx.datetime.DayOfWeek

@DslMarker
annotation class TasksListBuilderDSL

@TasksListBuilderDSL
class TasksBuilder {
    private val tasks = mutableListOf<Task>()

    fun task(builder: TaskBuilder.() -> Unit) {
        tasks += TaskBuilder().apply(builder).task
    }

    @TasksListBuilderDSL
    class TaskBuilder {
        fun name(name: String) {
            this.name = name
        }

        fun action(action: () -> Unit) {
            this.action = action
        }

        fun onFailure(action: (Exception) -> Unit) {
            this.onFailure = action
        }

        val TaskPriority.priority: Unit
            get() {
                this@TaskBuilder.priority = this
            }

        // ...
        var retries: Int = 0
            get() {
                return field
            }
            set(value) {
                field = value
                // save info about number of retries
            }
        // ...

        fun scheduled(builder: ScheduleBuilder.() -> TaskSchedule) {
            schedule = builder(ScheduleBuilder())
        }

        private var name: String = ""
        private var action: () -> Unit = { }
        private var onFailure: (Exception) -> Unit = { }
        private var priority = TaskPriority.DEFAULT
        private var schedule: List<ScheduleMoment> = emptyList()

        val task: Task
            get() = Task(
                name,
                priority = TaskPriority.DEFAULT,
                action = action,
                onFailure = onFailure,
                retries = retries,
                schedule = schedule
            )

    }

    class ScheduleBuilder {
        val mon = DayOfWeek.MONDAY
        val tue = DayOfWeek.TUESDAY
        val wen = DayOfWeek.WEDNESDAY
        val thu = DayOfWeek.THURSDAY
        val fri = DayOfWeek.FRIDAY
        val sat = DayOfWeek.SATURDAY
        val sun = DayOfWeek.SUNDAY

        fun sched(dayOfWeek: DayOfWeek, time: String) = listOf(ScheduleMoment(dayOfWeek, time))

        val DayOfWeek.midDay: TaskSchedule
            get() = sched(this, "12:00")

//        fun DayOfWeek.on(time: String) =
//            sched(this, time)

        infix fun DayOfWeek.on(time: String) =
            sched(this, time)

        operator fun DayOfWeek.times(time: String) =
            sched(this, time)

        infix fun ClosedRange<DayOfWeek>.on(time: String) =
            DayOfWeek.entries.filter { it in this }.map { ScheduleMoment(it, time) }

        operator fun TaskSchedule.plus(time: String): TaskSchedule {
            val days = map { it.dayOfWeek }.distinct()
            return this + days.map { ScheduleMoment(it, time) }
        }
    }

    fun build(): List<Task> = tasks
}

fun tasks(init: TasksBuilder.() -> Unit): List<Task> {
    val builder = TasksBuilder().apply(init)
    return builder.build()
}

fun exampleSchedule() {
    tasks {
        task {
            name("Task1")
            action { println("Running task 1") }
            scheduled {
                (mon on "12:00") +
                        (sat on "12:00")
            }
        }
        task {
            name("Task1")
            action { println("Running task 1") }
            scheduled {
                mon * "12:00" +
                        "20:00" + "04:00"
            }
        }
    }
}

fun exampleDSLMarker() {
    tasks {
        this.task {
            this@task.name("Task1")
            this@task.action { println("Running task 1") }
        }

        task {
            name("Task2")
            action { println("Running task 2") }
        }
    }
}

fun example0() {
    val tasksList = listOf(
        Task(
            name = "Task1",
            priority = TaskPriority.DEFAULT,
            action = { println("Running task 1") },
            onFailure = { e -> println("Failed to run task 1: ${e.message}") },
            retries = 1,
        ),
        // ...
        Task(
            name = "TaskCritical",
            priority = TaskPriority.HIGH,
            action = { println("Running critical task") },
            onFailure = { println("Failed to run critical task!!!") },
            retries = 10,
        ),
    )

    tasks {
        task {
            name("Task1")
            action({ println("Running task 1") })
            scheduled { mon..fri on "12:00" + mon.midDay }
        }
        task {
            name("TaskCritical")
            action { println("Running critical task") }
            onFailure { println("Failed to run critical task!!!") }
            TaskPriority.HIGH.priority
            retries = 10
        }
    }
}

fun example() {
    tasks {
        task {
            name("Test 1")
            // ...
        }
        task {
            name("Test 2")
            // ...
        }
    }
}