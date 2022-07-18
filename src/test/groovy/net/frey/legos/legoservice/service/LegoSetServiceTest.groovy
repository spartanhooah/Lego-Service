package net.frey.legos.legoservice.service

import net.frey.legos.legoservice.entity.LegoSet
import net.frey.legos.legoservice.repository.SetRepository
import net.frey.legos.legoservice.ro.BrickLinkSet
import net.frey.legos.legoservice.ro.Data
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import static java.util.UUID.randomUUID
import static net.frey.legos.legoservice.enums.Type.SET

class LegoSetServiceTest extends Specification {
    static final def CHEST_UUID = randomUUID()
    static final def DOLPHIN_UUID = randomUUID()
    static final def CHEST_NAME = "Build-N-Store Chest"
    static final def CHEST_NUMBER = "565-2"

    def setRepo = Mock(SetRepository)
    def partService = Mock(LegoPartService)
    def brickLinkService = Mock(BrickLinkService)

    def testSubject = new LegoSetService(setRepo, partService, brickLinkService)

    def chest = new LegoSet(
        CHEST_UUID,
        CHEST_NAME,
        CHEST_NUMBER,
        SET,
        1990
    )

    def dolphinAndTurtle = new LegoSet(
        DOLPHIN_UUID,
        "Dolphin and Turtle",
        "31128-1",
        SET,
        2022
    )

    def "get all sets"() {
        when:
        def result = testSubject.getAllSets()

        and:
        StepVerifier.create(result)
            .expectNextMatches(set -> set.name == CHEST_NAME)
            .expectNextMatches(set -> set.name == "Dolphin and Turtle")
            .verifyComplete()

        then:
        1 * setRepo.findAll() >> Flux.just(chest, dolphinAndTurtle)
    }

    def "get set by ID"() {
        when:
        def result = testSubject.getById(CHEST_UUID)

        and:
        StepVerifier.create(result)
            .expectNextMatches(set -> set.name == CHEST_NAME)
            .verifyComplete()

        then:
        1 * setRepo.findById(CHEST_UUID) >> Mono.just(chest)
    }

    def "get by set number"() {
        when:
        def result = testSubject.getByNumber(CHEST_NUMBER)

        and:
        StepVerifier.create(result)
            .expectNextMatches(set -> set.name == CHEST_NAME)
            .verifyComplete()

        then:
        1 * setRepo.findByNumber(CHEST_NUMBER) >> Flux.just(chest)
    }

    def "get set by name"() {
        when:
        def result = testSubject.getByName(CHEST_NAME)

        and:
        StepVerifier.create(result)
            .expectNextMatches(set -> set.name == CHEST_NAME)
            .verifyComplete()

        then:
        1 * setRepo.findByName(CHEST_NAME) >> Flux.just(chest)
    }

    def "get set by number and name"() {
        when:
        def result = testSubject.getByNumberAndName(CHEST_NUMBER, CHEST_NAME)

        and:
        StepVerifier.create(result)
            .expectNextMatches(set -> set.name == CHEST_NAME)
            .verifyComplete()

        then:
        1 * setRepo.findByNumberAndName(CHEST_NUMBER, CHEST_NAME) >> Flux.just(chest)
    }

    def "add a set"() {
        given:
        def chestEntity = new LegoSet(
            CHEST_UUID,
            CHEST_NAME,
            CHEST_NUMBER,
            SET,
            1990
        )

        def chestBL = new BrickLinkSet(
            new Data(
                CHEST_NUMBER,
                CHEST_NAME,
                SET,
                1990
            )
        )

        when:
        def result = testSubject.addSet(CHEST_NUMBER)

        and:
        StepVerifier.create(result)
            .verifyComplete()

        then:
        1 * brickLinkService.fetchSet(CHEST_NUMBER) >> Mono.just(chestBL)
        1 * partService.addSet(CHEST_NUMBER) >> Mono.empty()
        1 * setRepo.save(_ as LegoSet) >> Mono.just(chestEntity)
    }
}
