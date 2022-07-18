package net.frey.legos.legoservice.controller;

import lombok.RequiredArgsConstructor;
import net.frey.legos.legoservice.ro.SetResponse;
import net.frey.legos.legoservice.service.LegoSetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/sets")
@RequiredArgsConstructor
public class SetsController {
    private final LegoSetService legoSetService;

    @GetMapping
    public Flux<SetResponse> getSets(
        @RequestParam(required = false) String number,
        @RequestParam(required = false) String name
    ) {
        if (number != null) {
            if (validSetNumber(number)) {
                if (name != null) {
                    return legoSetService.getByNumberAndName(number, name);
                } else {
                    return legoSetService.getByNumber(number);
                }
            } else {
                return Flux.error(new IllegalArgumentException("Set numbers must match the regex \\d{1,5}(-\\d)?"));
            }
        } else if (name != null) {
            return legoSetService.getByName(name);
        } else {
            return legoSetService.getAllSets();
        }
    }

    @GetMapping("/{setId}")
    public Mono<SetResponse> getSetById(@PathVariable UUID setId) {
        return legoSetService.getById(setId);
    }

    private boolean validSetNumber(String setNumber) {
        return setNumber.matches("\\d{1,5}(-\\d)?");
    }
}
