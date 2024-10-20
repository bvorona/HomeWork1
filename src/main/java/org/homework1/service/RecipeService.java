package org.homework1.service;

import org.homework1.dto.IdNameDto;
import org.homework1.model.Ingredient;
import org.homework1.model.Recipe;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static org.homework1.constant.ValidationConstants.MAX_NAME_LENGTH;
import static org.homework1.constant.ValidationConstants.MAX_NUMBER_OF_INGREDIENTS;

public class RecipeService {

  private final Map<UUID, Ingredient> ingredientMap = new HashMap<>();
  private final Set<String> ingredientNames = new HashSet<>();
  private final Map<UUID, Recipe> recipeMap = new HashMap<>();
  private final Set<String> recipeNames = new HashSet<>();
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final Lock readLock = lock.readLock();
  private final Lock writeLock = lock.writeLock();

  public Ingredient createIngredient(String name) {
    writeLock.lock();
    try {
      return createIngredientInternal(name);
    } finally {
      writeLock.unlock();
    }
  }

  private Ingredient createIngredientInternal(String name) {
    validateName(name);
    if (ingredientNames.contains(name)) {
      throw new IllegalArgumentException("Ingredient with name " + name + " already exists");
    }
    Ingredient ingredient = new Ingredient(name);
    ingredientMap.put(ingredient.getId(), ingredient);
    ingredientNames.add(name);
    return ingredient;
  }

  public void removeIngredient(UUID id) {
    writeLock.lock();
    try {
      removeIngredientInternal(id);
    } finally {
      writeLock.unlock();
    }
  }

  private void removeIngredientInternal(UUID id) {
    final Set<String> usedInRecipes = recipeMap.values().stream()
        .filter(recipe -> recipe.getIngredients().contains(id))
        .map(Recipe::getName)
        .collect(Collectors.toSet());
    if (!usedInRecipes.isEmpty()) {
      throw new IllegalArgumentException("Ingredient is used in recipes: " + usedInRecipes);
    }
    final Ingredient removed = ingredientMap.remove(id);
    if (removed == null) {
      throw new IllegalArgumentException("Ingredient with id " + id + " not found");
    }
    ingredientNames.remove(removed.getName());
  }

  public String getIngredientName(UUID id) {
    readLock.lock();
    try {
      return Optional.ofNullable(ingredientMap.get(id))
          .map(Ingredient::getName)
          .orElseThrow(() -> new IllegalArgumentException("Ingredient with id " + id + " not found"));
    } finally {
      readLock.unlock();
    }
  }

  public UUID createRecipe(String name, List<UUID> ingredients) {
    writeLock.lock();
    try {
      return createRecipeInternal(name, ingredients);
    } finally {
      writeLock.unlock();
    }
  }

  private UUID createRecipeInternal(String name, List<UUID> ingredients) {
    validateRecipeName(name);
    validateRecipeIngredientsInternal(ingredients);
    Recipe recipe = new Recipe(name, ingredients);
    recipeMap.put(recipe.getId(), recipe);
    recipeNames.add(name);
    return recipe.getId();
  }

  public void removeRecipe(UUID id) {
    writeLock.lock();
    try {
      removeRecipeInternal(id);
    } finally {
      writeLock.unlock();
    }
  }

  private void removeRecipeInternal(UUID id) {
    final Recipe removed = recipeMap.remove(id);
    if (removed == null) {
      throw new IllegalArgumentException("Recipe with id " + id + " not found");
    }
    recipeNames.remove(removed.getName());
  }

  public void updateRecipe(UUID id, String name, List<UUID> ingredients) {
    writeLock.lock();
    try {
      updateRecipeInternal(id, name, ingredients);
    } finally {
      writeLock.unlock();
    }
  }

  private void updateRecipeInternal(UUID id, String name, List<UUID> ingredients) {
    validateRecipeName(name);
    validateRecipeIngredientsInternal(ingredients);
    final Recipe recipe = recipeMap.get(id);
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe with id " + id + " not found");
    }
    recipe.setName(name);
    recipe.setIngredients(ingredients);
    recipeNames.remove(recipe.getName());
    recipeNames.add(name);
  }

  public List<UUID> getRecipeIngredients(UUID id) {
    readLock.lock();
    try {
      return Optional.ofNullable(recipeMap.get(id))
          .map(Recipe::getIngredients)
          .orElseThrow(() -> new IllegalArgumentException("Recipe with id " + id + " not found"));
    } finally {
      readLock.unlock();
    }
  }

  public List<IdNameDto> listRecipes() {
    readLock.lock();
    try {
      return recipeMap.values().stream()
          .map(recipe -> new IdNameDto(recipe.getId(), recipe.getName()))
          .sorted(Comparator.comparing(IdNameDto::name))
          .collect(Collectors.toList());
    } finally {
      readLock.unlock();
    }
  }

  public List<String> viewRecipe(UUID id) {
    readLock.lock();
    try {
      return Optional.ofNullable(recipeMap.get(id))
          .map(recipe -> recipe.getIngredients().stream()
              .map(ingredientMap::get)
              .map(Ingredient::getName)
              .collect(Collectors.toList()))
          .orElseThrow(() -> new IllegalArgumentException("Recipe with id " + id + " not found"));
    } finally {
      readLock.unlock();
    }
  }

  private void validateRecipeName(String name) {
    validateName(name);
    if (recipeNames.contains(name)) {
      throw new IllegalArgumentException("Recipe with name " + name + " already exists");
    }
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name is required and cannot be blank");
    }
    if (name.length() > MAX_NAME_LENGTH) {
      throw new IllegalArgumentException("Name cannot be longer than " + MAX_NAME_LENGTH + "characters");
    }
  }

  public void validateRecipeIngredients(List<UUID> ingredients) {
    readLock.lock();
    try {
      validateRecipeIngredientsInternal(ingredients);
    } finally {
      readLock.unlock();
    }
  }

  private void validateRecipeIngredientsInternal(List<UUID> ingredients) {
    if (ingredients == null || ingredients.isEmpty()) {
      throw new IllegalArgumentException("Recipe must have at least one ingredient");
    }
    if (ingredients.size() > MAX_NUMBER_OF_INGREDIENTS) {
      throw new IllegalArgumentException("Recipe cannot have more than 10 ingredients");
    }
    final Set<UUID> unknownIngredients = ingredients.stream()
        .filter(ingredient -> !ingredientMap.containsKey(ingredient))
        .collect(Collectors.toSet());
    if (!unknownIngredients.isEmpty()) {
      throw new IllegalArgumentException("Unknown ingredients: " + unknownIngredients);
    }
  }
}
