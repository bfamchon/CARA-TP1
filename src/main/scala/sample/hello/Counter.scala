package sample.hello

import akka.actor.Actor

object Counter {
  final case class ManageRows(row: String)
  case object GetOccurrences

}

class Counter extends Actor {
  val occurencesByWord = collection.mutable.Map[String, Int]().withDefaultValue(0)

  def receive = {
    case Counter.ManageRows(row) =>
      row.split(" ").foreach(word => addWordToMap(word))
    case Counter.GetOccurrences =>
      sender ! occurencesByWord
  }
  private def addWordToMap(word: String): Unit = {
      occurencesByWord(word.toLowerCase()) += 1
  }
}