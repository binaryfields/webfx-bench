package org.binaryfields.webfx

import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
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

@Component
class TweetApi(private val tweetService: TweetService) {

	suspend fun list(request: ServerRequest): ServerResponse =
		ServerResponse.ok().json().bodyValue(tweetService.list()).awaitSingle()
}

@Configuration
class RouterConfiguration {

	@Bean
	fun tweetRoutes(tweetApi: TweetApi) = coRouter {
		GET("/v1/tweets", tweetApi::list)
	}
}

@SpringBootApplication
class WebfxApplication

fun main(args: Array<String>) {
	runApplication<WebfxApplication>(*args)
}
