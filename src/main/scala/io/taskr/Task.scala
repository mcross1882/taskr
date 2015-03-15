package io.taskr

import com.owlike.genson.defaultGenson.{fromJson, toJson}

case class Event(name: String, description: String) {
    override def toString(): String = toJson(this)
}

case class Task(id: String, events: Seq[Event], currentEvent: Int) {
    override def toString(): String = toJson(this)
}

case class TaskProgress(task: Task) {
    val totalEvents = task.events.length
    val currentEvent = task.currentEvent
    val remainingEvents = totalEvents - currentEvent
    val progress =
        if (0 != totalEvents) currentEvent.toFloat / totalEvents.toFloat else 0

    override def toString(): String = {
        val event = if (task.events.contains(currentEvent)) task.events(currentEvent) else null

        toJson(Map(
            "total_events" -> totalEvents,
            "current_event" -> currentEvent,
            "remaining_events" -> remainingEvents,
            "progress" -> progress,
            "event" -> event
        ))
    }
}
