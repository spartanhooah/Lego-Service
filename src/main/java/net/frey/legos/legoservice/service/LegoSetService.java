package net.frey.legos.legoservice.service;

import lombok.RequiredArgsConstructor;
import net.frey.legos.legoservice.entity.LegoSet;
import net.frey.legos.legoservice.repository.SetRepository;
import net.frey.legos.legoservice.ro.SetResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class LegoSetService {
    private final SetRepository setRepository;
    private final LegoPartService partService;
    private final BrickLinkService brickLinkService;

    public Flux<SetResponse> getAllSets() {
        return setRepository.findAll().map(entityToRO());
    }

    public Mono<SetResponse> getById(UUID setId) {
        return setRepository.findById(setId).map(entityToRO());
    }

    public Flux<SetResponse> getByNumber(String setNumber) {
        return setRepository.findByNumber(setNumber).map(entityToRO());
    }

    public Flux<SetResponse> getByName(String setName) {
        return setRepository.findByName(setName).map(entityToRO());
    }

    public Flux<SetResponse> getByNumberAndName(String setNumber, String setName) {
        return setRepository.findByNumberAndName(setNumber, setName).map(entityToRO());
    }

    public Mono<Void> addSet(String setNumber) {
        return brickLinkService.fetchSet(setNumber)
            .flatMap(brickLinkSet -> {
                LegoSet set = new LegoSet();

                set.setNumber(setNumber);
                set.setName(brickLinkSet.data().name());
                set.setType(brickLinkSet.data().type());
                set.setYear(brickLinkSet.data().year());

                return setRepository.save(set);
            })
            .flatMap(legoSet -> partService.addSet(setNumber));
    }

    private Function<LegoSet, SetResponse> entityToRO() {
        return legoSet -> new SetResponse(legoSet.getId(), legoSet.getName(), legoSet.getNumber(), legoSet.getYear());
    }
}
