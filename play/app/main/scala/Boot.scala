package org.binaryfields.webfx

import java.util.{Timer, TimerTask}
import java.util.concurrent.atomic.AtomicLong
import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.api.libs.json._

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

@Singleton
class TweetService @Inject()() {

  private val counter = new AtomicLong(1000000L)

  def list()(implicit ec: ExecutionContext): Future[Seq[Tweet]] = {
    for {
      _ <- Deferred.delay(8L)(true)
    } yield {
      (1 to 5).map { index =>
        Tweet(counter.getAndIncrement(), s"author$index", "Hello, World!")
      }
    }
  }
}

case class TweetCounter(id: Long, counter: Long)

@Singleton
class TweetCounters @Inject()() {

  private val counter = new AtomicLong(1000L)

  def fetchCounter(tweetId: Long)(implicit ec: ExecutionContext): Future[TweetCounter] = {
    for {
      _ <- Future.successful(())
    } yield {
      TweetCounter(tweetId, counter.getAndIncrement())
    }
  }
}

trait TweetProtocol {
  case class ListResponse(tweets: Seq[Tweet], counters: Seq[TweetCounter])

  implicit val tweetFormatter = Json.writes[Tweet]
  implicit val tweetCounterFormatter = Json.writes[TweetCounter]
  implicit val listResponseFormatter = Json.writes[ListResponse]
}

@Singleton
class TweetApi @Inject()(cc: ControllerComponents, tweetCounters: TweetCounters, tweetService: TweetService)(implicit ec: ExecutionContext) extends AbstractController(cc)
  with TweetProtocol {

  def list() = Action.async { _ =>
    for {
      tweets <- tweetService.list()
      counterOps = tweets.map(tweet => tweetCounters.fetchCounter(tweet.id))
      counters <- Future.sequence(counterOps)
    } yield {
      Ok(Json.toJson(ListResponse(tweets, counters)))
    }
  }
}
