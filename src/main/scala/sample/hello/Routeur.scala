package sample.hello

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._


object Router {

  final case class GetRow(row: String)

  final case class ReceiveMap(map: collection.mutable.Map[String, Int])

  final case class RouteLine(line: String)

  case object GetFullMap

}

class Router extends Actor with ActorLogging {
  var arrayOfCountersRef = new Array[ActorRef](3)
  val system = ActorSystem("Main")
  var occurencesByWord = collection.mutable.Map[String, Int]().withDefaultValue(0)
  var cmp = 0
  implicit val timeout: Timeout = 5 seconds


  override def preStart(): Unit = {
    arrayOfCountersRef(0) = system.actorOf(Props[Counter], "Counter0")
    arrayOfCountersRef(1) = system.actorOf(Props[Counter], "Counter1")
    arrayOfCountersRef(2) = system.actorOf(Props[Counter], "Counter2")

    log.info("Counter are initialized !")

    val future = arrayOfCountersRef(1) ? Counter.GetOccurrences
    println(Await.result(future, timeout.duration).asInstanceOf[collection.mutable.Map[String, Int]])

  }

  def receive = {
    case Router.GetRow(number) =>
      log.info("There is this much readers: " + number)
    case Router.ReceiveMap(mapFromCounter) =>
      occurencesByWord ++ mapFromCounter.map { case (k, v) => k -> (v + occurencesByWord.getOrElse(k, 0)) }
      println("ReceiveMap: " + mapFromCounter)
    case Router.RouteLine(line) =>
      arrayOfCountersRef(cmp % arrayOfCountersRef.length) ! Counter.ManageRows(line)
      cmp += 1
    case Router.GetFullMap =>


//      arrayOfCountersRef.foreach(actor => {
//        val future = actor ? Counter.GetOccurrences
//        val result = Await.result(future, timeout.duration).asInstanceOf[collection.mutable.Map[String, Int]]
//        (occurencesByWord.keySet ++ result.keySet).map {i=> (i,occurencesByWord.getOrElse(i,0) + result.getOrElse(i,0))}.toMap
//      })
//    println("GetFullMap: " + occurencesByWord)
    sender ! occurencesByWord
  }

}