package io.taskr

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import com.owlike.genson.defaultGenson.{fromJson, toJson}

class TaskServiceActorTest extends Specification with Specs2RouteTest with TaskService {
    def actorRefFactory = system

    private var taskId: String = ""

    "TaskService" should {
        "return a new task for POST request to the /task endpoint" in {
            Post("/task") ~> endpoints ~> check {
                val task = fromJson[JsonResponse[Task]](responseAs[String])
                taskId = task.data.id
                task.status must beEqualTo(200)
                task.message must beEqualTo("Created new task")
                task.data.id must be matching("""\w+-\w+-\w+-\w+-\w+""")
                task.data.events must beEmpty
                task.data.current_event must beEqualTo(0)
            }
        }
    }
}
