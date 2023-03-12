package io.binaryfields.http4s

import cats.Traverse
import cats.effect.{Async, IO, IOApp, Sync}
import cats.implicits.*
import com.comcast.ip4s.{ipv4, port}
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.{EntityEncoder, HttpRoutes}
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s.blaze.server.BlazeServerBuilder
// import org.http4s.netty.server.NettyServerBuilder
// import org.http4s.blaze.server.BlazeServerBuilder
// import org.http4s.ember.server.EmberServerBuilder
// import org.http4s.server.middleware.Logger

import java.util.concurrent.atomic.AtomicLong
import scala.concurrent.{ExecutionContext, Future}

case class Tweet(id: Long, author: String, content: String)

case class TweetCounter(id: Long, counter: Long)

class TweetService[F[_]: Async]:
  private val counter = new AtomicLong(1000000L)

  def list(): F[Seq[Tweet]] =
    for {
      _ <- Sync[F].pure(())
    } yield {
      (1 to 5).map { index =>
        Tweet(counter.getAndIncrement(), s"author$index", "Hello, World!")
      }
    }

class TweetCounters[F[_]: Sync]:
  private val counter = new AtomicLong(1000L)

  def fetchCounter(tweetId: Long): F[TweetCounter] =
    for {
      _ <- Sync[F].pure(())
    } yield {
      TweetCounter(tweetId, counter.getAndIncrement())
    }

object TweetProtocol:
  case class ListResponse(tweets: Seq[Tweet], counters: Seq[TweetCounter])

  object ListResponse:
    given Encoder[ListResponse] = deriveEncoder[ListResponse]

    given [F[_]]: EntityEncoder[F, ListResponse] =
      jsonEncoderOf[ListResponse]

  implicit val tweetFormatter: Encoder[Tweet] = deriveEncoder[Tweet]
  implicit val tweetCounterFormatter: Encoder[TweetCounter] = deriveEncoder[TweetCounter]

object TweetApi:

  def routes[F[_]: Sync](
      tweetCounters: TweetCounters[F],
      tweetService: TweetService[F]
  ): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] { case GET -> Root / "v1" / "tweets" =>
      for {
        tweets <- tweetService.list()
        counters <- tweets.traverse(tweet => tweetCounters.fetchCounter(tweet.id))
        next <- Sync[F].pure(Seq.empty)
        results <- Sync[F].pure(next)
        response <- Ok(TweetProtocol.ListResponse(tweets, counters))
      } yield response
    }

object TweetServer:

  def run[F[_]: Async]: F[Nothing] = {
    val tweetService = new TweetService()
    val tweetCounters = new TweetCounters()
    val httpApp = TweetApi.routes[F](tweetCounters, tweetService).orNotFound
    // val withLogging = Logger.httpApp(true, true)(httpApp)
    for {
      _ <-
        BlazeServerBuilder[F]
          .bindHttp(8080, "0.0.0.0")
          .withHttpApp(httpApp)
          .resource
    } yield ()
  }.useForever

object Boot extends IOApp.Simple:
  val run: IO[Nothing] = TweetServer.run[IO]

/*
EmberServerBuilder.default[F]
  .withHost(ipv4"0.0.0.0")
  .withPort(port"8080")
  .withHttpApp(httpApp)
  .build

BlazeServerBuilder[F]
  .bindHttp(8080, "0.0.0.0")
  .withHttpApp(httpApp)
  .resource

NettyServerBuilder[F]
  .bindHttp(8080, "0.0.0.0")
  .withHttpApp(httpApp)
  .withNioTransport
  .resource
 */
