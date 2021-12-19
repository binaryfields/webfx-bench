package org.binaryfields.webfx

import java.util.concurrent.atomic.AtomicLong

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.can.Http
import spray.routing._
import spray.httpx.SprayJsonSupport
import spray.json._

case class Tweet(id: Long, author: String, content: String)

object TweetService {
  val counter = new AtomicLong(1000000L)

  def list(): Seq[Tweet] = {
    Seq(
      Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
    )
  }
}

trait TweetProtocol extends DefaultJsonProtocol {
  implicit val tweetFormatter = jsonFormat3(Tweet.apply)
}

trait TweetApi extends HttpService with TweetProtocol with SprayJsonSupport {
  val routes = pathPrefix("v1") {
    path("tweets") {
      get {
        complete {
          TweetService.list()
        }
      }
    }
  }
}

class TweetApiActor extends Actor with TweetApi {
  def actorRefFactory = context

  def receive = runRoute(routes)
}

object Boot {

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5.seconds)

  val service = system.actorOf(Props[TweetApiActor], "TweetApi")

  def main(args: Array[String]): Unit = {
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
    ()
  }
}
