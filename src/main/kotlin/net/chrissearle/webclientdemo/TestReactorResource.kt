package net.chrissearle.webclientdemo

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/reactor")
class TestReactorResource(val service: ReactorService) {
    @GetMapping
    fun getPost(): Mono<String> {
        return service.getList().flatMap { todos ->
            val id = todos?.first()?.id ?: return@flatMap Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND))

            val item = service.getItem(id)

            item.map { todo ->
                todo.title
            }
        }
    }
}

@Component
class ReactorService(val webClientBuilder: WebClient.Builder) {
    fun getList() = webClientBuilder
        .build()
        .get()
        .uri("https://jsonplaceholder.typicode.com/todos/")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(object : ParameterizedTypeReference<List<Todo?>?>() {})

    fun getItem(id: Int) = webClientBuilder
        .build()
        .get()
        .uri("https://jsonplaceholder.typicode.com/todos/$id")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(Todo::class.java)
}