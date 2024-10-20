package org.homework1.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pancake {
  private final UUID id;
  private final List<String> ingredients;

  public Pancake(List<String> ingredients) {
    this.id = UUID.randomUUID();
    if (ingredients == null || ingredients.isEmpty()) {
      throw new IllegalArgumentException("Recipe must have at least one ingredient");
    }
    this.ingredients = ingredients;
  }

  public String description() {
    return "Delicious pancake with %s!".formatted(String.join(", ", ingredients));
  }

  public UUID getId() {
    return id;
  }

  public List<String> getIngredients() {
    return ingredients;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pancake pancake = (Pancake) o;
    return Objects.equals(id, pancake.id) && Objects.equals(ingredients, pancake.ingredients);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ingredients);
  }
}
