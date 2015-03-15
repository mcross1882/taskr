package io.taskr

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot {

    implicit val system = ActorSystem("taskr")

    implicit val timeout = Timeout(5.seconds)

    def main(args: Array[String]) {
        if (args.length < 2) {
            return println("taskr [host] [port]")
        }

        val service = system.actorOf(Props[TaskServiceActor], "task-service")

        IO(Http) ? Http.Bind(service, interface = args(2), port = args(1).toInt)
    }
}
