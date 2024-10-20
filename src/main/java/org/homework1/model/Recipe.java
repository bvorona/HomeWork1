package org.homework1.model;

import java.util.*;

public class Recipe {

  private final UUID id;
  private String name;
  private List<UUID> ingredients;

  public Recipe(String name, List<UUID> ingredients) {
    this.id = UUID.randomUUID();
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Recipe name is required and cannot be blank");
    }
    if (ingredients == null || ingredients.isEmpty()) {
      throw new IllegalArgumentException("Recipe must have at least one ingredient");
    }
    this.name = name;
    this.ingredients = ingredients;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<UUID> getIngredients() {
    return new ArrayList<>(ingredients);
  }

  public void setIngredients(List<UUID> ingredients) {
    this.ingredients = ingredients;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Recipe recipe = (Recipe) o;
    return Objects.equals(id, recipe.id) && Objects.equals(name, recipe.name) && Objects.equals(ingredients, recipe.ingredients);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, ingredients);
  }
}
