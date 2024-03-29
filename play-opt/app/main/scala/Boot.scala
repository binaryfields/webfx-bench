package org.binaryfields.webfx

import java.util.concurrent.atomic.AtomicLong
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import play.api.http.{ContentTypes, HttpEntity, Status}
import play.api.mvc._

case class Tweet(id: Long, author: String, content: String)

case class TweetCounter(id: Long, counter: Long)

@Singleton
class TweetService @Inject() () {

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
class TweetCounters @Inject() () {

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
}

@Singleton
class TweetApi @Inject() (
    cc: ControllerComponents,
    tweetCounters: TweetCounters,
    tweetService: TweetService
)(implicit
    ec: ExecutionContext
) extends AbstractController(cc)
    with JsonResults
    with TweetProtocol {

  def list() = Action.async { _ =>
    for {
      tweets <- tweetService.list()
      counterOps = tweets.map(tweet => tweetCounters.fetchCounter(tweet.id))
      counters <- Future.sequence(counterOps)
    } yield {
      JsonOk(ListResponse(tweets, counters))
    }
  }
}

trait JsonResults {

  private val mapper = new ObjectMapper()
    .registerModule(DefaultScalaModule)

  class JsonStatus(status: Int)
      extends Result(header = ResponseHeader(status), body = HttpEntity.NoEntity) {

    def apply[C](content: AnyRef): Result = {
      val header = ResponseHeader(status)
      val json = mapper.writeValueAsBytes(content)
      val body = HttpEntity.Strict(ByteString(json), Some(ContentTypes.JSON))
      Result(header, body)
    }
  }

  val JsonOk = new JsonStatus(Status.OK)
}
