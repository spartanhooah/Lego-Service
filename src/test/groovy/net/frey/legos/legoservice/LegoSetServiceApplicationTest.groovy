package net.frey.legos.legoservice

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class LegoSetServiceApplicationTest extends Specification {
    def "The application context loads successfully"() {
        when:
        null

        then:
        noExceptionThrown()
    }
}