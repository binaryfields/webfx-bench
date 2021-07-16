package io.digitalstream.webfx

import java.util.concurrent.atomic.AtomicLong

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._

case class Tweet(id: Long, author: String, content: String)

object TweetService {
  val counter = new AtomicLong(1000000L)

  def list(): Seq[Tweet] = {
    Seq(
      Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
    )
  }
}

object Boot {

  def getTweets: Endpoint[Seq[Tweet]] = get("tweets") {
    Ok(TweetService.list())
  }

  def main(args: Array[String]): Unit = {
    val api = getTweets.toServiceAs[Application.Json]
    val server = Http.server.serve("localhost:8080", api)
    Await.ready(server)
  }
}
