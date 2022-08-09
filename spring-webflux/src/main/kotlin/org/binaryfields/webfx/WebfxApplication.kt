package org.binaryfields.webfx

import kotlinx.coroutines.future.await
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicLong

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
class WebfxApplication

fun main(args: Array<String>) {
    runApplication<WebfxApplication>(*args)
}
