package io.taskr

import com.redis.RedisClient
import com.owlike.genson.defaultGenson.{fromJson, toJson}

final class TaskRegistry(client: RedisClient) {

    private val TaskLifetime = 60 * 60 * 24

    def newTask(): Task = {
        var id = makeUUID
        while (client.exists(id)) {
            id = makeUUID
        }

        val task = Task(id, Seq.empty[Event], 0)
        client.set(id, toJson(task))
        client.expire(id, TaskLifetime)
        task
    }

    def deleteTask(id: String) {
        client.del(id)
    }

    def hasTask(id: String): Boolean = client.exists(id)

    def newTaskEvent(taskId: String, name: String, description: String): Task = {
        val eventToAdd = Event(name, description)
        val task = findTask(taskId)
        val updatedTask = Task(task.id, task.events ++ Seq(eventToAdd), task.currentEvent)
        client.set(taskId, toJson(updatedTask))
        updatedTask
    }

    def tickNextEvent(taskId: String): Task = {
        val task = findTask(taskId)
        if (task.currentEvent >= task.events.length) {
            return task
        }

        val updatedTask = Task(task.id, task.events, task.currentEvent + 1)
        client.set(taskId, toJson(updatedTask))
        updatedTask
    }

    def findTask(taskId: String): Task = {
        client.get(taskId) match {
            case Some(rawTaskJson) => fromJson[Task](rawTaskJson)
            case None => throw new Exception(s"Failed to find task with id $taskId")
        }
    }

    protected def makeUUID(): String = java.util.UUID.randomUUID.toString
}
