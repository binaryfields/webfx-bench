package org.binaryfields.webfx

import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
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

trait TweetApi extends TweetProtocol with SprayJsonSupport {

  implicit val ec: ExecutionContext

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
