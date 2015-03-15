package io.taskr

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class TaskServiceActorTest extends Specification with Specs2RouteTest with TaskService {
    def actorRefFactory = system
  
    "TaskService" should {
        "return a new task for PUT requests to the /task/new endpoint" in {
            Put("/task/new") ~> endpoints ~> check {
                responseAs[String] must be matching(""".*"id":"\w+-\w+-\w+-\w+-\w+".*""")
            }
        }
    }
}
