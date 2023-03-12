package org.binaryfields.webfx

import java.util.concurrent.atomic.AtomicLong
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

import play.api.mvc._
import play.api.libs.json._

case class Tweet(id: Long, author: String, content: String)

case class TweetCounter(id: Long, counter: Long)

@Singleton
class TweetService @Inject()() {

  private val counter = new AtomicLong(1000000L)

  def list()(implicit ec: ExecutionContext): Future[Seq[Tweet]] = {
    for {
      _ <- Future.successful(())
    } yield {
      (1 to 5).map { index =>
        Tweet(counter.getAndIncrement(), s"author$index", "Hello, World!")
      }
    }
  }
}

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
