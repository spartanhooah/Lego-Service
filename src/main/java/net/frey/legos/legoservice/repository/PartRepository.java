package net.frey.legos.legoservice.repository;

import net.frey.legos.legoservice.entity.LegoPart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface PartRepository extends ReactiveCrudRepository<LegoPart, UUID> {
}
