package org.homework1.model;

import java.util.Objects;
import java.util.UUID;

public class Ingredient {
    private final UUID id;
    private final String name;

  public Ingredient(String name) {
    this.id = UUID.randomUUID();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name is required and cannot be blank");
        }
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Ingredient that = (Ingredient) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
