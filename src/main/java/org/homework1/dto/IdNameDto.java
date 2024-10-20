package org.homework1.dto;

import java.util.Objects;
import java.util.UUID;

public record IdNameDto(UUID id, String name) {

  public IdNameDto {
        Objects.requireNonNull(id, "Id must not be null");
        Objects.requireNonNull(name, "Name must not be null");
    }
}
