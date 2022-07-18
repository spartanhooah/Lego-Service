package net.frey.legos.legoservice.service

import net.frey.legos.legoservice.oauth.BLAuthSigner
import net.frey.legos.legoservice.ro.BrickLinkSet
import net.frey.legos.legoservice.ro.Data
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import spock.lang.Specification

import static net.frey.legos.legoservice.enums.Type.SET

@SpringBootTest
class BrickLinkServiceTest extends Specification {
    static final def CHEST_NAME = "Build-N-Store Chest"
    static final def CHEST_NUMBER = "565-2"

    def testSubject = new BrickLinkService()

    def params = [
        oauth_nonce           : "98e75f4f-93c4-4528-a19f-53a092f1a35a",
        oauth_signature       : "2q5lorbexT2hQQBgGkZXcM0pk5Y=",
        oauth_token           : "085570E5CDE04B78B9CEE81B5D5C5AF1",
        oauth_consumer_key    : "2D171B0D2D0E41799DAD4AB6BE9AD2A6",
        oauth_version         : "1.0",
        oauth_timestamp       : "1657756484",
        oauth_signature_method: "HMAC-SHA1"
    ]

    def "fetch a set"() {
        given:
        def chest = new BrickLinkSet(
            new Data(
                CHEST_NUMBER,
                CHEST_NAME,
                SET,
                1990
            )
        )

        when:
        def result = testSubject.fetchSet(CHEST_NUMBER)

        and:
        StepVerifier.create(result)
            .expectNextMatches(set -> set.data.name == CHEST_NAME)
            .verifyComplete()

        then:
        1 * BLAuthSigner.getFinalOAuthParams(*_) >> params
    }
}
