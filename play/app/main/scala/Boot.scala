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
class TweetService @Inject() () {

  private val counter = new AtomicLong(1000000L)

  def list()(implicit ec: ExecutionContext): Future[Seq[Tweet]] = {
    for {
      _ <- Deferred.delay(16L)(true)
    } yield {
      Seq(
        Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
      )
    }
  }
}

trait TweetProtocol {
  implicit val tweetWrites = Json.writes[Tweet]
}

@Singleton
class TweetApi @Inject() (cc: ControllerComponents, tweetService: TweetService)(implicit
    ec: ExecutionContext
) extends AbstractController(cc)
    with TweetProtocol {

  def list() = Action.async { _ =>
    for {
      tweets <- tweetService.list()
    } yield {
      Ok(Json.toJson(tweets))
    }
  }
}
