package net.frey.legos.legoservice.service;

import lombok.RequiredArgsConstructor;
import net.frey.legos.legoservice.repository.PartRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LegoPartService {
    private final PartRepository partRepository;

    public Mono<Void> addSet(String setNumber) {
        return null;
    }
}
