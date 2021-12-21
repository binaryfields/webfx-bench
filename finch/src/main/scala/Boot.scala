package org.binaryfields.webfx

import java.util.{Timer, TimerTask}
import java.util.concurrent.atomic.AtomicLong

import com.twitter.finagle.Http
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._

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

object Boot {

  import io.finch.syntax.scala.scalaToTwitterFuture

  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val tweetService = new TweetService()

  def getTweets = path("v1") :: get("tweets") {
      tweetService.list().map(Ok)
  }

  def main(args: Array[String]): Unit = {
    val api = getTweets.toServiceAs[Application.Json]
    val server = Http.server.serve("localhost:8080", api)
    Await.ready(server)
  }
}
