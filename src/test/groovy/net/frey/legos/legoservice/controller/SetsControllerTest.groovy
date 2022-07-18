package net.frey.legos.legoservice.controller

import net.frey.legos.legoservice.ro.SetResponse
import net.frey.legos.legoservice.service.LegoSetService
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

class SetsControllerTest extends Specification {
    static final def setUuid = UUID.randomUUID()
    LegoSetService legoService = Stub()
    def testSubject = new SetsController(legoService)
    def webClient = WebTestClient.bindToController(testSubject).build()

    def darkShark = new SetResponse(setUuid,
        "Dark Shark",
        "6679-1",
        new URL("https://www.bricklink.com/v2/catalog/catalogitem.page?S=6679-1"))

    def "Can get all sets"() {
        given:
        legoService.getAllSets() >> Flux.just(darkShark)

        expect:
        webClient.get().uri("/sets")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(SetResponse)
            .consumeWith({ response ->
                matchesDarkSharkFromList(response)
            })
    }

    def "Can get a set by ID"() {
        given:
        legoService.getById(setUuid) >> Mono.just(darkShark)

        expect:
        webClient.get().uri("/sets/$setUuid")
            .exchange()
            .expectStatus().isOk()
            .expectBody(SetResponse)
            .consumeWith({ response ->
                response.responseBody.name() == "Dark Shark"
                response.responseBody.number() == "6679-1"
                response.responseBody.bricklinkUrl() == new URL("https://www.bricklink.com/v2/catalog/catalogitem.page?S=6679-1")
            })
    }

    def "Can get a set by exact number"() {
        given:
        legoService.getByNumber("6679-1") >> Flux.just(darkShark)

        expect:
        webClient.get()
            .uri({ builder -> builder.path("/sets").queryParam("number", "6679-1").build() })
            .exchange()
            .expectBodyList(SetResponse)
            .consumeWith({ response ->
                matchesDarkSharkFromList(response)
            })
    }

    def "Can get a set by exact name"() {
        given:
        legoService.getByName("Dark Shark") >> Flux.just(darkShark)

        expect:
        webClient.get()
            .uri({ builder -> builder.path("/sets").queryParam("name", "Dark Shark").build() })
            .exchange()
            .expectBodyList(SetResponse)
            .consumeWith({ response ->
                matchesDarkSharkFromList(response)
            })
    }

    def "Can get a set by exact number and exact name"() {
        given:
        legoService.getByNumberAndName("6679-1", "Dark Shark") >> Flux.just(darkShark)

        expect:
        webClient.get()
            .uri({ builder ->
                builder.path("/sets")
                    .queryParam("number", "6679-1")
                    .queryParam("name", "Dark Shark")
                    .build()
            })
            .exchange()
            .expectBodyList(SetResponse)
            .consumeWith({ response ->
                matchesDarkSharkFromList(response)
            })
    }

    private matchesDarkSharkFromList(response) {
        assert response.responseBody.size() == 1
        assert response.responseBody[0].name() == "Dark Shark"
        assert response.responseBody[0].number() == "6679-1"
        assert response.responseBody[0].bricklinkUrl() == new URL("https://www.bricklink.com/v2/catalog/catalogitem.page?S=6679-1")
    }
}
