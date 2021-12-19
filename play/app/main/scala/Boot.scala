package org.binaryfields.webfx

import java.util.concurrent.atomic.AtomicLong
import javax.inject.{Inject, Singleton}

import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import play.api.http.{ContentTypes, HttpEntity, Status}
import play.api.mvc._

import scala.concurrent.ExecutionContext

case class Tweet(id: Long, author: String, content: String)

@Singleton
class TweetService @Inject() () {
  val counter = new AtomicLong(1000000L)

  def list(): Seq[Tweet] = {
    Seq(
      Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
    )
  }
}

@Singleton
class TweetApi @Inject() (cc: ControllerComponents, tweetService: TweetService)(implicit
    ec: ExecutionContext
) extends AbstractController(cc)
    with JsonResults {

  def list() = Action { req =>
    JsonOk(tweetService.list())
  }
}

trait JsonResults {

  val mapper = new ObjectMapper()
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
