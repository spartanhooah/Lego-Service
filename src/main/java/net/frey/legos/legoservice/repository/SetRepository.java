package net.frey.legos.legoservice.repository;

import net.frey.legos.legoservice.entity.LegoSet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SetRepository extends ReactiveCrudRepository<LegoSet, UUID> {
    Flux<LegoSet> findByNumber(String setNumber);
    Flux<LegoSet> findByName(String setName);
    Flux<LegoSet> findByNumberAndName(String setNumber, String setName);
}
