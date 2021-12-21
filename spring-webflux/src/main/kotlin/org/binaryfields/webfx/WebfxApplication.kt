package org.binaryfields.webfx

import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

data class Tweet(val id: Long, val author: String, val content: String)

@Service
class TweetService {

	private val counter = AtomicLong(1000000L)

	suspend fun list(): List<Tweet> {
		delay(16L)
		return listOf(
			Tweet(counter.getAndIncrement(), "author1", "Hello, World!")
		)
	}
}

@RestController
class TweetController(private val tweetService: TweetService) {

	@GetMapping("/v1/tweets")
	suspend fun findAll(): List<Tweet> {
		return tweetService.list()
	}
}

@SpringBootApplication
class WebfxApplication

fun main(args: Array<String>) {
	runApplication<WebfxApplication>(*args)
}
