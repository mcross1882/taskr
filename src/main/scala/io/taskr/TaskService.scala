package io.taskr

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.util.LoggingContext
import com.redis.RedisClient
import com.owlike.genson.defaultGenson.{fromJson, toJson}

class TaskServiceActor extends Actor with TaskService {
    implicit def handler(implicit log: LoggingContext) = {
        ExceptionHandler {
            case e: Exception => {
                requestUri { uri =>
                    log.warning("Request to {} caught exception {}", uri, e.getMessage)
                    e.printStackTrace
                    complete(s"""{"status":500,"message":"${e.getMessage}","stack_trace":${toJson(e.getStackTrace.take(10))}}""")
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
    path("task" / "new") {
        put {
            respondWithMediaType(`application/json`) {
                complete {
                    val task = registry.newTask
                    task.toString
                }
            }
        }
    } ~
    path("task" / Segment / "delete") { (id) =>
        delete {
            respondWithMediaType(`application/json`) {
                complete {
                    if (registry.hasTask(id)) {
                        registry.deleteTask(id)
                        s"""{"status":200,"message":"Successfully deleted task $id"}"""
                    } else {
                        s"""{"status":404,"message":"Task $id does not exist"}"""
                    }
                }
            }
        }
    } ~
    path("task" / Segment / "progress") { (id) =>
        get {
            respondWithMediaType(`application/json`) {
                complete {
                    val task = registry.findTask(id)
                    TaskProgress(task).toString
                }
            }
        }
    } ~
    path("task" / Segment / "event" / "new") { (id) =>
        put {
            respondWithMediaType(`application/json`) {
                parameters('name, 'description) { (name, description) =>
                    complete {
                        toJson(registry.newTaskEvent(id, name, description))
                    }
                }
            }
        }
    } ~
    path("task" / Segment / "event" / "tick") { (id) =>
        post {
            respondWithMediaType(`application/json`) {
                complete {
                    toJson(registry.tickNextEvent(id))
                }
            }
        }
    }
}
