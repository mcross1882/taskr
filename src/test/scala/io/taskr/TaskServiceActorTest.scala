package io.taskr

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import com.owlike.genson.defaultGenson.{fromJson, toJson}

class TaskServiceActorTest extends Specification with Specs2RouteTest with TaskService {
    def actorRefFactory = system

    "TaskService" should {
        "return a new task for POST request to the /task endpoint" in {
            Post("/task") ~> endpoints ~> check {
                responseAs[String] must be matching(""".*"id":"\w+-\w+-\w+-\w+-\w+".*""")
            }
        }

        "delete an existing task with a DELETE request to /task/:id" in {
            val task = createNewTask
      
            Delete(s"/task/${task.id}") ~> endpoints ~> check {
                responseAs[String] must contain("Successfully deleted task ${task.id}")
            }
        }
    }

    protected def createNewTask(): Task = {
        Post("/task") ~> endpoints ~> check {
            println(responseAs[String])
            return fromJson[Task](responseAs[String])
        }
    }
}
