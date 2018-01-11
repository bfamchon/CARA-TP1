package sample.hello

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Terminated

object Main{

  def main(args: Array[String]): Unit = {
    val map1 = Map(1 -> 9 , 2 -> 20)
    val map2 = Map(1 -> 100, 3 -> 300)

    map1 ++ map2.map{ case (k,v) => k -> (v + map1.getOrElse(k,0)) }
    println("map1" + map1)
    println("map2" + map2)

    val system = ActorSystem("Main")
    system.actorOf(Props[Reader], "Reader")
  }

  class Terminator(ref: ActorRef) extends Actor with ActorLogging {
    context watch ref
    def receive = {
      case Terminated(_) =>
        log.info("{} has terminated, shutting down system", ref.path)
        context.system.terminate()
    }
  }

}