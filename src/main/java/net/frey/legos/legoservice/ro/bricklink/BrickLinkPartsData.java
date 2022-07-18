package net.frey.legos.legoservice.ro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrickLinkPartsData(List<BrickLinkPart> entries) {
}
