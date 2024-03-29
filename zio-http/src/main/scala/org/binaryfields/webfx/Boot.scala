package org.binaryfields.webfx

import zhttp.http.*
import zhttp.service.Server
import zio.*
import zio.json.*

import java.util.concurrent.atomic.AtomicLong

case class Tweet(id: Long, author: String, content: String)

case class TweetCounter(id: Long, counter: Long)

class TweetService {

  private val counter = new AtomicLong(1000000L)

  def list(): Task[Seq[Tweet]] = {
    ZIO.succeed {
      (1 to 5).map { index =>
        Tweet(counter.getAndIncrement(), s"author$index", "Hello, World!")
      }
    }
  }
}

object TweetService {
  def layer: ZLayer[Any, Nothing, TweetService] =
    ZLayer.succeed(new TweetService())
}

class TweetCounters {

  private val counter = new AtomicLong(1000L)

  def fetchCounter(tweetId: Long): Task[TweetCounter] = {
    ZIO.succeed(TweetCounter(tweetId, counter.getAndIncrement()))
  }
}

object TweetCounters {
  def layer: ZLayer[Any, Nothing, TweetCounters] =
    ZLayer.succeed(new TweetCounters())
}

trait TweetProtocol {
  case class ListResponse(tweets: Seq[Tweet], counters: Seq[TweetCounter])

  implicit val tweetEncoder: JsonEncoder[Tweet] = DeriveJsonEncoder.gen[Tweet]
  implicit val tweetCounterEncoder: JsonEncoder[TweetCounter] = DeriveJsonEncoder.gen[TweetCounter]
  implicit val listResponseEncoder: JsonEncoder[ListResponse] = DeriveJsonEncoder.gen[ListResponse]
}

object TweetApi extends TweetProtocol {
  def apply(): Http[TweetService & TweetCounters, Throwable, Request, Response] =
    Http.collectZIO[Request] { case Method.GET -> !! / "v1" / "tweets" =>
      for {
        tweets <- ZIO.serviceWithZIO[TweetService](_.list())
        counterOps = tweets.map { tweet =>
          ZIO.serviceWithZIO[TweetCounters](_.fetchCounter(tweet.id))
        }
        counters <- ZIO.collectAll(counterOps)
      } yield {
        Response.json(ListResponse(tweets, counters).toJson)
      }
    }
}

object MainApp extends ZIOAppDefault {
  def run: ZIO[Any, Throwable, Nothing] =
    Server
      .start(
        port = 8080,
        http = TweetApi()
      )
      .provide(
        TweetService.layer,
        TweetCounters.layer
      )
}
