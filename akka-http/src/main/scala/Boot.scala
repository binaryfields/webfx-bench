package io.digitalstream.webfx

import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json._

case class Tweet(id: Long, author: String, content: String)

object TweetService {
  val counter = new AtomicLong(1000000L)

  def list(): List[Tweet] = {
    List(
      Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
    )
  }
}

trait TweetProtocol extends DefaultJsonProtocol {
  implicit val tweetFormatter = jsonFormat3(Tweet.apply)
}

trait TweetApi extends TweetProtocol with SprayJsonSupport {

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

object Boot extends TweetApi {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {
    Http()(system).bindAndHandle(
      handler = routes,
      interface = "localhost",
      port = 8080
    )(materializer)
    ()
  }
}
