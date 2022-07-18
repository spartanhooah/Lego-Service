package net.frey.legos.legoservice.ro;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.frey.legos.legoservice.enums.Type;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrickLinkSetData(String no, String name, Type type, @JsonAlias("year_released") int year) {
}
