package net.chrissearle.webclientdemo

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

@RestController
@RequestMapping("/suspend")
class TestSuspendResource(val service: SuspendService) {
    @GetMapping
    suspend fun getList() = service.getItem(service.getList().first().id).title
}

@Component
class SuspendService(val webClientBuilder: WebClient.Builder) {
    suspend fun getList() = webClientBuilder
        .build()
        .get()
        .uri("https://jsonplaceholder.typicode.com/todos/")
        .accept(MediaType.APPLICATION_JSON)
        .awaitExchange {
            it.awaitBody<List<Todo>>()
        }

    suspend fun getItem(id: Int) = webClientBuilder
        .build()
        .get()
        .uri("https://jsonplaceholder.typicode.com/todos/$id")
        .accept(MediaType.APPLICATION_JSON)
        .awaitExchange {
            it.awaitBody<Todo>()
        }
}