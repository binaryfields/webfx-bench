package org.binaryfields.webfx

import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._
import scala.util.Try

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.binaryfields.webfx.Boot.system
import spray.can.Http
import spray.routing._
import spray.httpx.SprayJsonSupport
import spray.json._

case class Tweet(id: Long, author: String, content: String)

case class TweetCounter(id: Long, counter: Long)

class TweetService()(implicit val ec: ExecutionContext) {

  private val counter = new AtomicLong(1000000L)

  def list(): Future[Seq[Tweet]] = {
    for {
      _ <- Future.successful(())
    } yield {
      (1 to 5).map { index =>
        Tweet(counter.getAndIncrement(), s"author$index", "Hello, World!")
      }
    }
  }
}

class TweetCounters()(implicit val ec: ExecutionContext) {

  private val counter = new AtomicLong(1000L)

  def fetchCounter(tweetId: Long): Future[TweetCounter] = {
    for {
      _ <- Future.successful(())
    } yield {
      TweetCounter(tweetId, counter.getAndIncrement())
    }
  }
}

trait TweetProtocol extends DefaultJsonProtocol {
  case class ListResponse(tweets: Seq[Tweet], counters: Seq[TweetCounter])

  implicit val tweetFormatter = jsonFormat3(Tweet.apply)
  implicit val tweetCounterFormatter = jsonFormat2(TweetCounter.apply)
  implicit val listResponseFormatter = jsonFormat2(ListResponse.apply)
}

trait TweetApi extends HttpService with TweetProtocol with SprayJsonSupport {

  implicit val ec2: ExecutionContext = ExecutionContext.global
  
  lazy val tweetService = new TweetService()
  lazy val tweetCounters = new TweetCounters()

  val routes = pathPrefix("v1") {
    path("tweets") {
      get {
        complete {
          for {
            tweets <- tweetService.list()
            counterOps = tweets.map(tweet => tweetCounters.fetchCounter(tweet.id))
            counters <- Future.sequence(counterOps)
          } yield {
            ListResponse(tweets, counters)
          }
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
