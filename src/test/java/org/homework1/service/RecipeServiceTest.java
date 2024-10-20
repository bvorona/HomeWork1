package org.homework1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.homework1.dto.IdNameDto;
import org.homework1.model.Ingredient;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.homework1.constant.ValidationConstants.MAX_NAME_LENGTH;
import static org.homework1.constant.ValidationConstants.MAX_NUMBER_OF_INGREDIENTS;

public class RecipeServiceTest {
  private static final String DARK_CHOCOLATE = "Dark chocolate";
  private static final String DARK_CHOCOLATE_PANCAKE = "Dark chocolate pancake";

  private RecipeService recipeService;

  @BeforeEach
  public void setUp() {
    recipeService = new RecipeService();
  }

  @Test
  public void testCreateIngredient_success() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    String name = recipeService.getIngredientName(ingredient.getId());

    assertNotNull(ingredient, "Ingredient should not be null");
    assertEquals(DARK_CHOCOLATE, name, "Ingredient name should match");
  }

  @Test
  public void testCreateIngredient_duplicateName_throwsException() {
    recipeService.createIngredient(DARK_CHOCOLATE);
    assertThrows(IllegalArgumentException.class, () ->
        recipeService.createIngredient(DARK_CHOCOLATE), "Should throw exception for duplicate ingredient name");
  }

  @Test
  public void testCreateIngredient_nullName_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        recipeService.createIngredient(null), "Should throw exception for null name");
  }

  @Test
  public void testCreateIngredient_blankName_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        recipeService.createIngredient("   "), "Should throw exception for blank name");
  }

  @Test
  public void testCreateIngredient_nameTooLong_throwsException() {
    String longName = "a".repeat(MAX_NAME_LENGTH + 1);
    assertThrows(IllegalArgumentException.class, () -> recipeService.createIngredient(longName),
        "Should throw exception for name exceeding max length");
  }

  @Test
  public void testRemoveIngredient_success() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    recipeService.removeIngredient(ingredient.getId());

    assertThrows(IllegalArgumentException.class, () -> recipeService.getIngredientName(ingredient.getId()),
        "Should throw exception when accessing removed ingredient");
  }

  @Test
  public void testRemoveIngredient_usedInRecipe_throwsException() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());
    recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients);

    assertThrows(IllegalArgumentException.class, () -> recipeService.removeIngredient(ingredient.getId()),
        "Should throw exception when removing ingredient used in a recipe");
  }

  @Test
  public void testCreateRecipe_success() {
    Ingredient ingredient1 = recipeService.createIngredient(DARK_CHOCOLATE);
    Ingredient ingredient2 = recipeService.createIngredient("Whipped cream");
    List<UUID> ingredients = Arrays.asList(ingredient1.getId(), ingredient2.getId());

    UUID recipeId = recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients);

    final List<UUID> createdRecipeIngredients = recipeService.getRecipeIngredients(recipeId);
    final String createdRecipeName = recipeService.listRecipes().stream().filter(recipe -> recipe.id().equals(recipeId)).findFirst()
        .map(IdNameDto::name).orElse(null);

    assertNotNull(recipeId, "Recipe should not be null");
    assertEquals(DARK_CHOCOLATE_PANCAKE, createdRecipeName, "Recipe name should match");
    assertEquals(2, createdRecipeIngredients.size(), "Recipe should have two ingredients");
    assertTrue(createdRecipeIngredients.contains(ingredient1.getId()), "Recipe should contain Dark chocolate");
    assertTrue(createdRecipeIngredients.contains(ingredient2.getId()), "Recipe should contain Whipped cream");
  }

  @Test
  public void testUpdateRecipe_success() {
    Ingredient ingredient1 = recipeService.createIngredient(DARK_CHOCOLATE);
    Ingredient ingredient2 = recipeService.createIngredient("Whipped cream");
    List<UUID> ingredients1 = Collections.singletonList(ingredient1.getId());
    List<UUID> ingredients2 = Arrays.asList(ingredient1.getId(), ingredient2.getId());

    final UUID recipeId = recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients1);
    recipeService.updateRecipe(recipeId, "Pancake Updated", ingredients2);

    List<UUID> updatedIngredients = recipeService.getRecipeIngredients(recipeId);
    final String updatedRecipeName = recipeService.listRecipes().stream().filter(recipe -> recipe.id().equals(recipeId)).findFirst()
        .map(IdNameDto::name).orElse(null);

    assertEquals("Pancake Updated", updatedRecipeName, "Recipe name should match");
    assertEquals(2, updatedIngredients.size(), "Recipe should have two ingredients");
    assertTrue(updatedIngredients.contains(ingredient1.getId()), "Recipe should contain Dark chocolate");
    assertTrue(updatedIngredients.contains(ingredient2.getId()), "Recipe should contain Whipped cream");
  }

  @Test
  public void testCreateRecipe_duplicateName_throwsException() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());
    recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients);

    assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients),
        "Should throw exception for duplicate recipe name");
  }

  @Test
  public void testCreateRecipe_invalidIngredientId_throwsException() {
    UUID invalidId = UUID.randomUUID();
    List<UUID> ingredients = Collections.singletonList(invalidId);

    assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients),
        "Should throw exception for invalid ingredient ID");
  }

  @Test
  public void testCreateRecipe_nullName_throwsException() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());

    assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe(null, ingredients),
        "Should throw exception for null recipe name");
  }

  @Test
  public void testCreateRecipe_blankName_throwsException() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());

    assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe("   ", ingredients),
        "Should throw exception for blank recipe name");
  }

  @Test
  public void testCreateRecipe_nameTooLong_throwsException() {
    String longName = "a".repeat(MAX_NAME_LENGTH + 1);
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());

    assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe(longName, ingredients),
        "Should throw exception for name exceeding max length");
  }

  @Test
  public void testCreateRecipe_noIngredients_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, null),
        "Should throw exception for null ingredients list");
  }

  @Test
  public void testCreateRecipe_emptyIngredients_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, new ArrayList<>()),
        "Should throw exception for empty ingredients list");
  }

  @Test
  public void testCreateRecipe_tooManyIngredients_throwsException() {
    // Create MAX_NUMBER_OF_INGREDIENTS + 1 ingredients
    List<UUID> ingredientIds = new ArrayList<>();
    for (int i = 0; i <= MAX_NUMBER_OF_INGREDIENTS; i++) {
      Ingredient ingredient = recipeService.createIngredient("Ingredient" + i);
      ingredientIds.add(ingredient.getId());
    }

    assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredientIds),
        "Should throw exception when exceeding max number of ingredients");
  }

  @Test
  public void testUpdateRecipe_invalidId_throwsException() {
    UUID invalidId = UUID.randomUUID();
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());

    assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(invalidId, DARK_CHOCOLATE_PANCAKE, ingredients),
        "Should throw exception for invalid recipe ID");
  }

  @Test
  public void testUpdateRecipe_duplicateName_throwsException() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Arrays.asList(ingredient.getId());
    recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients);
    final UUID recipe2Id = recipeService.createRecipe("New pancake", ingredients);

    assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(recipe2Id, DARK_CHOCOLATE_PANCAKE, ingredients),
        "Should throw exception for duplicate recipe name");
  }

  @Test
  public void testRemoveRecipe_success() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());
    final UUID recipeId = recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients);

    recipeService.removeRecipe(recipeId);

    assertThrows(IllegalArgumentException.class, () -> recipeService.getRecipeIngredients(recipeId),
        "Should throw exception when accessing removed recipe");
  }

  @Test
  public void testRemoveRecipe_invalidId_throwsException() {
    UUID invalidId = UUID.randomUUID();
    assertThrows(IllegalArgumentException.class, () -> recipeService.removeRecipe(invalidId),
        "Should throw exception for invalid recipe ID");
  }

  @Test
  public void testGetRecipeIngredients_invalidId_throwsException() {
    UUID invalidId = UUID.randomUUID();
    assertThrows(IllegalArgumentException.class, () -> recipeService.getRecipeIngredients(invalidId),
        "Should throw exception for invalid recipe ID");
  }

  @Test
  public void testListRecipes_success() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());
    recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients);
    recipeService.createRecipe("New pancake", ingredients);

    List<IdNameDto> recipes = recipeService.listRecipes();

    assertEquals(2, recipes.size(), "Should list two recipes");
    assertEquals(DARK_CHOCOLATE_PANCAKE, recipes.get(0).name(), "First recipe should be 'Dark schooled pancake'");
    assertEquals("New pancake", recipes.get(1).name(), "Second recipe should be 'New pancake'");
  }

  @Test
  public void testViewRecipe_success() {
    Ingredient ingredient1 = recipeService.createIngredient(DARK_CHOCOLATE);
    Ingredient ingredient2 = recipeService.createIngredient("Whipped cream");
    List<UUID> ingredients = Arrays.asList(ingredient1.getId(), ingredient2.getId());

    final UUID recipeId = recipeService.createRecipe(DARK_CHOCOLATE_PANCAKE, ingredients);
    List<String> ingredientNames = recipeService.viewRecipe(recipeId);

    assertEquals(2, ingredientNames.size(), "Should have two ingredient names");
    assertTrue(ingredientNames.contains(DARK_CHOCOLATE), "Ingredient names should contain 'Dark chocolate'");
    assertTrue(ingredientNames.contains("Whipped cream"), "Ingredient names should contain 'Whipped cream'");
  }

  @Test
  public void testViewRecipe_invalidId_throwsException() {
    UUID invalidId = UUID.randomUUID();

    assertThrows(IllegalArgumentException.class, () -> recipeService.viewRecipe(invalidId),
        "Should throw exception for invalid recipe ID");
  }

  @Test
  public void testValidateRecipeIngredients_success() {
    Ingredient ingredient = recipeService.createIngredient(DARK_CHOCOLATE);
    List<UUID> ingredients = Collections.singletonList(ingredient.getId());

    assertDoesNotThrow(() -> recipeService.validateRecipeIngredients(ingredients),
        "Should not throw exception for valid ingredients");
  }

  @Test
  public void testValidateRecipeIngredients_nullIngredients_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> recipeService.validateRecipeIngredients(null),
        "Should throw exception for null ingredients list");
  }

  @Test
  public void testValidateRecipeIngredients_emptyIngredients_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> recipeService.validateRecipeIngredients(new ArrayList<>()),
        "Should throw exception for empty ingredients list");
  }

  @Test
  public void testValidateRecipeIngredients_invalidIngredient_throwsException() {
    recipeService.createIngredient(DARK_CHOCOLATE);
    UUID invalidId = UUID.randomUUID();
    List<UUID> ingredients = Collections.singletonList(invalidId);

    assertThrows(IllegalArgumentException.class, () -> recipeService.validateRecipeIngredients(ingredients), "Should throw exception for invalid ingredient ID");
  }

  @Test
  public void testValidateRecipeIngredients_tooManyIngredients_throwsException() {
    // Create MAX_NUMBER_OF_INGREDIENTS + 1 ingredients
    List<UUID> ingredientIds = new ArrayList<>();
    for (int i = 0; i <= MAX_NUMBER_OF_INGREDIENTS; i++) {
      Ingredient ingredient = recipeService.createIngredient("Ingredient" + i);
      ingredientIds.add(ingredient.getId());
    }

    assertThrows(IllegalArgumentException.class, () -> recipeService.validateRecipeIngredients(ingredientIds),
        "Should throw exception when exceeding max number of ingredients");
  }
}
