package org.binaryfields.webfx

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.suspendCancellableCoroutine
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.*
import reactor.netty.DisposableChannel
import reactor.netty.http.server.HttpServer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.CompletionStage
import java.util.concurrent.atomic.AtomicLong
import java.util.function.BiConsumer
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> CompletionStage<T>.await(): T {
  val future = toCompletableFuture() // retrieve the future
  // slow path -- suspend
  return suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
    val consumer = ContinuationConsumer(cont)
    whenComplete(consumer)
    cont.invokeOnCancellation {
      future.cancel(false)
      consumer.cont = null // shall clear reference to continuation to aid GC
    }
  }
}

private class ContinuationConsumer<T>(
  @Volatile @JvmField var cont: Continuation<T>?
) : BiConsumer<T?, Throwable?> {
  @Suppress("UNCHECKED_CAST")
  override fun accept(result: T?, exception: Throwable?) {
    val cont = this.cont ?: return // atomically read current value unless null
    if (exception == null) {
      // the future has completed normally
      cont.resume(result as T)
    } else {
      // the future has completed with an exception, unwrap it to provide consistent view of .await() result and to propagate only original exception
      cont.resumeWithException((exception as? CompletionException)?.cause ?: exception)
    }
  }
}

data class Tweet(val id: Long, val author: String, val content: String)

class TweetService {

  private val counter = AtomicLong(1000000L)

  suspend fun list(): List<Tweet> {
    val count = CompletableFuture.completedFuture(5).await()
    return (1..count).map { index ->
      Tweet(counter.getAndIncrement(), "author$index", "Hello, World!")
    }
  }
}

data class TweetCounter(val id: Long, val counter: Long)

class TweetCounters {

  private val counter = AtomicLong(1000L)

  suspend fun fetchCounter(tweetId: Long): TweetCounter {
    val count = CompletableFuture.completedFuture(counter.getAndIncrement()).await()
    return TweetCounter(tweetId, count)
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
