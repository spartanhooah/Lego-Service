package net.frey.legos.legoservice.ro;

import java.util.UUID;

public record SetResponse(UUID id, String name, String number, int year) {
}
