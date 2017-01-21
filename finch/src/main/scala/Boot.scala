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

  def list(): List[Tweet] = {
    List(
      Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
    )
  }
}

object Boot extends App {

  def getTweets: Endpoint[List[Tweet]] = get("tweets") {
    Ok(TweetService.list())
  }

  val api: Service[Request, Response] = getTweets.toServiceAs[Application.Json]

  val server = Http.server
    .serve("localhost:8080", api)

  Await.ready(server)
}
