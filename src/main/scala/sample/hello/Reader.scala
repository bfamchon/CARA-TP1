package sample.hello

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.Await
import scala.io.Source


object Reader {
  case object Initialize
}

class Reader extends Actor {
  val system = ActorSystem("Main")
  var router = system.actorOf(Props[Router], "Routeur")
  override def preStart(): Unit = {
    val source = Source.fromFile("text.txt")
    for (line <- source.getLines()) {
      router ! Router.RouteLine(line)
    }
    implicit val timeout: Timeout = 10 seconds

    val future = router ? Router.GetFullMap
    println("Result : " + Await.result(future, timeout.duration).asInstanceOf[collection.mutable.Map[String, Int]])
    source.close
  }

  def receive = {
    case Reader.Initialize =>
      println("Counter initialized")

  }
}

