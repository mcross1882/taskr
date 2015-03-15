package io.taskr

import com.owlike.genson.defaultGenson.{fromJson, toJson}

case class Event(name: String, description: String) {
    override def toString(): String = toJson(this)
}

case class Task(id: String, events: Seq[Event], current_event: Int) {
    override def toString(): String = toJson(this)
}

case class TaskProgress(task: Task) {
    val totalEvents = task.events.length
    val currentEvent = task.currentEvent
    val remainingEvents = totalEvents - currentEvent
    val progress =
        if (0 != totalEvents) currentEvent.toFloat / totalEvents.toFloat else 0

    def toMap() = {
        val event = if (task.events.contains(currentEvent)) task.events(currentEvent) else null

        Map(
            "total_events" -> totalEvents,
            "current_event" -> currentEvent,
            "remaining_events" -> remainingEvents,
            "progress" -> progress,
            "event" -> event
        )
    }
}
