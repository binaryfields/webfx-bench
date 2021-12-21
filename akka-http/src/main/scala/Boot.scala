package org.binaryfields.webfx

import java.util.{Timer, TimerTask}
import java.util.concurrent.atomic.AtomicLong
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

object Deferred {

  private val timer = new Timer()

  def delay[T](ms: Long)(block: => T): Future[T] = {
    val promise = Promise[T]()
    timer.schedule(
      new TimerTask {
        override def run(): Unit = {
          promise.complete(Try(block))
        }
      },
      ms
    )
    promise.future
  }
}

case class Tweet(id: Long, author: String, content: String)

class TweetService()(implicit val ec: ExecutionContext) {

  private val counter = new AtomicLong(1000000L)

  def list(): Future[Seq[Tweet]] = {
    for {
      _ <- Deferred.delay(16L)(true)
    } yield {
      Seq(
        Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
      )
    }
  }
}

trait TweetProtocol extends DefaultJsonProtocol {
  implicit val tweetFormatter = jsonFormat3(Tweet.apply)
}

trait TweetApi extends TweetProtocol with SprayJsonSupport {

  implicit val ec: ExecutionContext

  lazy val tweetService = new TweetService()

  val routes = pathPrefix("v1") {
    path("tweets") {
      get {
        complete {
          tweetService.list()
        }
      }
    }
  }
}

object Boot extends TweetApi {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  lazy val ec: ExecutionContext = system.dispatcher

  def main(args: Array[String]): Unit = {
    Http()(system).bindAndHandle(
      handler = routes,
      interface = "localhost",
      port = 8080
    )(materializer)
    ()
  }
}
