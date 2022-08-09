package org.binaryfields.webfx

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.suspendCancellableCoroutine
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.EnableWebFlux;
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

@Service
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

@Service
class TweetCounters {

    private val counter = AtomicLong(1000L)

    suspend fun fetchCounter(tweetId: Long): TweetCounter {
        return TweetCounter(tweetId, counter.getAndIncrement())
    }
}

data class ListResponse(val tweets: List<Tweet>, val counters: List<TweetCounter>)

@RestController
class TweetController(private val tweetCounters: TweetCounters, private val tweetService: TweetService) {

    @GetMapping("/v1/tweets")
    suspend fun findAll(): ListResponse {
        val tweets = tweetService.list()
        val counters = tweets.map { tweet -> tweetCounters.fetchCounter(tweet.id) }
        return ListResponse(tweets, counters)
    }
}

@SpringBootApplication
@EnableWebFlux
class WebfxApplication

fun main(args: Array<String>) {
    runApplication<WebfxApplication>(*args)
}
