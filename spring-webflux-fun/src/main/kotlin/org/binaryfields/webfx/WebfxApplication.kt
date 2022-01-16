package org.binaryfields.webfx

import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.*
import reactor.netty.DisposableChannel
import reactor.netty.http.server.HttpServer
import java.util.concurrent.atomic.AtomicLong

data class Tweet(val id: Long, val author: String, val content: String)

class TweetService {

  private val counter = AtomicLong(1000000L)

  suspend fun list(): List<Tweet> {
    delay(16L)
    return listOf(
      Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
    )
  }
}

class TweetApi(private val tweetService: TweetService) {

  suspend fun list(request: ServerRequest): ServerResponse =
    ServerResponse
      .ok()
      .json()
      .bodyValue(tweetService.list())
      .awaitSingle()
}

class RouterConfiguration {

  fun tweetRoutes(tweetApi: TweetApi) = coRouter {
    accept(MediaType.APPLICATION_JSON).nest {
      GET("/v1/tweets", tweetApi::list)
    }
  }
}

object WebfxApplication {

  @JvmStatic
  fun main(args: Array<String>) {
    val tweetService = TweetService()
    val tweetApi = TweetApi(tweetService)
    val routerConfig = RouterConfiguration()
    val httpHandler = RouterFunctions.toHttpHandler(routerConfig.tweetRoutes(tweetApi))
    val reactorHttpHandler = ReactorHttpHandlerAdapter(httpHandler)

    HttpServer.create()
      .port(8080)
      .handle(reactorHttpHandler)
      .bind()
      .flatMap(DisposableChannel::onDispose)
      .block()
  }
}
