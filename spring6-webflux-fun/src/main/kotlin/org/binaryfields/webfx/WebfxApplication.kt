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
    delay(8L)
    return (1..5).map { index ->
      Tweet(counter.getAndIncrement(), "author$index", "Hello, World!")
    }
  }
}

data class TweetCounter(val id: Long, val counter: Long)

class TweetCounters {

  private val counter = AtomicLong(1000L)

  suspend fun fetchCounter(tweetId: Long): TweetCounter {
    return TweetCounter(tweetId, counter.getAndIncrement())
  }
}

data class ListResponse(val tweets: List<Tweet>, val counters: List<TweetCounter>)

class TweetApi(
  private val tweetCounters: TweetCounters,
  private val tweetService: TweetService
) {

  suspend fun list(request: ServerRequest): ServerResponse {
    val tweets = tweetService.list()
    val counters = tweets.map { tweet -> tweetCounters.fetchCounter(tweet.id) }
    return ServerResponse
      .ok()
      .json()
      .bodyValue(ListResponse(tweets, counters))
      .awaitSingle()
  }
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
    val tweetCounters = TweetCounters()
    val tweetApi = TweetApi(tweetCounters, tweetService)
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
