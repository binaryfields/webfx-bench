package org.binaryfields.webfx

import java.util.{Timer, TimerTask}
import java.util.concurrent.atomic.AtomicLong
import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.binaryfields.webfx.Boot.system
import spray.can.Http
import spray.routing._
import spray.httpx.SprayJsonSupport
import spray.json._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._
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

trait TweetApi extends HttpService with TweetProtocol with SprayJsonSupport {

  implicit val ec2: ExecutionContext = ExecutionContext.global
  
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
