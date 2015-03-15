package io.taskr

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.util.LoggingContext
import com.redis.RedisClient
import com.owlike.genson.defaultGenson.{fromJson, toJson}

case class JsonResponse[T](status: Int, message: String, data: T) {
    override def toString(): String = toJson(Map(
        "status" -> status,
        "message" -> message,
        "data" -> data
    ))
}

class TaskServiceActor extends Actor with TaskService {
    implicit def handler(implicit log: LoggingContext) = {
        ExceptionHandler {
            case e: Exception => {
                requestUri { uri =>
                    log.warning("Request to {} caught exception {}", uri, e.getMessage)
                    e.printStackTrace
                    complete(s"""{"status":500,"message":"${e.getMessage}","errors":${toJson(e.getStackTrace.take(10))}}""")
                }
            }
        }
    }

    def actorRefFactory = context

    def receive = runRoute(endpoints)
}

trait TaskService extends HttpService {

    private val registry = new TaskRegistry(new RedisClient("localhost", 6379))

    val endpoints =
    path("task") {
        post {
            respondWithMediaType(`application/json`) {
                complete {
                    val task = registry.newTask
                    JsonResponse(200, s"Created new task", task).toString
                }
            }
        }
    } ~
    path("task" / Segment) { (id) =>
        delete {
            respondWithMediaType(`application/json`) {
                complete {
                    if (registry.hasTask(id)) {
                        registry.deleteTask(id)
                        JsonResponse(200, s"Successfully deleted task $id", null).toString
                    } else {
                        JsonResponse(404, s"Task $id does not exist", null).toString
                    }
                }
            }
        } ~
        get {
            respondWithMediaType(`application/json`) {
                complete {
                    if (!registry.hasTask(id)) {
                        JsonResponse(404, s"Task $id does not exist", null).toString
                    } else {
                        val task = registry.findTask(id)
                        JsonResponse(200, s"Found task", TaskProgress(task).toMap).toString
                    }
                }
            }
        }
    } ~
    path("task" / Segment / "event" / "new") { (id) =>
        post {
            respondWithMediaType(`application/json`) {
                parameters('name, 'description) { (name, description) =>
                    complete {
                        val task = registry.newTaskEvent(id, name, description)
                        JsonResponse(200, s"Added new event to task", task).toString
                    }
                }
            }
        }
    } ~
    path("task" / Segment / "event" / "tick") { (id) =>
        post {
            respondWithMediaType(`application/json`) {
                complete {
                    val task = registry.tickNextEvent(id)
                    JsonResponse(200, s"Moved to next event for task", task).toString
                }
            }
        }
    }
}
