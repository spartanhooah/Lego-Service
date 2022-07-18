package net.frey.legos.legoservice.ro;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrickLinkSubsetResponse(@JsonAlias("data") BrickLinkPartsData data) {
}
