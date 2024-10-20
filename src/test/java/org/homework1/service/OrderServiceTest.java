package org.homework1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.homework1.constant.OrderStatus;
import org.homework1.model.Ingredient;
import org.homework1.model.Pancake;
import org.homework1.util.IntegerRange;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

  private OrderService orderService;
  private BuildingService buildingService;
  private RecipeService recipeService;
  private UUID ingredient1Id;
  private UUID ingredient2Id;
  private UUID recipeId;

  @BeforeEach
  public void setUp() throws Exception {
    orderService = new OrderService();

    // Use reflection to access buildingService and recipeService
    Field buildingServiceField = OrderService.class.getDeclaredField("buildingService");
    buildingServiceField.setAccessible(true);
    buildingService = (BuildingService) buildingServiceField.get(orderService);

    Field recipeServiceField = OrderService.class.getDeclaredField("recipeService");
    recipeServiceField.setAccessible(true);
    recipeService = (RecipeService) recipeServiceField.get(orderService);

    // Set up data in buildingService and recipeService
    buildingService.addBuilding(1, new IntegerRange(101, 199));
    Ingredient ingredient1 = recipeService.createIngredient("Dark chocolate");
    Ingredient ingredient2 = recipeService.createIngredient("Whipped cream");
    ingredient1Id = ingredient1.getId();
    ingredient2Id = ingredient2.getId();
    List<UUID> ingredients = Arrays.asList(ingredient1Id, ingredient2Id);
    recipeId = recipeService.createRecipe("Sweet Pancake", ingredients);
  }

  @Test
  public void testCreateOrder_success() {
    UUID orderId = orderService.createOrder(1, 101);
    assertNotNull(orderId, "Order ID should not be null");

    assertEquals(OrderStatus.DRAFT, orderService.getOrderStatus(orderId));
  }

  @Test
  public void testCreateOrder_invalidBuilding_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(2, 101),
        "Should throw exception for invalid building number");
  }

  @Test
  public void testCreateOrder_invalidRoom_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1, 201),
        "Should throw exception for invalid room number");
  }

  @Test
  public void testAddPancake_success() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    List<Pancake> pancakes = orderService.getPancakes(orderId);

    assertEquals(1, pancakes.size(), "Order should have one pancake");
  }

  @Test
  public void testCompleteOrder_success() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    orderService.completeOrder(orderId);
    Set<UUID> completedOrders = orderService.listCompletedOrders();

    assertEquals(OrderStatus.COMPLETED, orderService.getOrderStatus(orderId));
    assertTrue(completedOrders.contains(orderId), "Order should be in completed orders");
  }

  @Test
  public void testPrepareOrder_success() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    orderService.completeOrder(orderId);
    orderService.prepareOrder(orderId);

    assertEquals(OrderStatus.PREPARED, orderService.getOrderStatus(orderId));
    Set<UUID> preparedOrders = orderService.listPreparedOrders();
    Set<UUID> completedOrders = orderService.listCompletedOrders();

    assertTrue(preparedOrders.contains(orderId), "Order should be in prepared orders");
    assertFalse(completedOrders.contains(orderId), "Order should not be in completed orders");
  }

  @Test
  public void testDeliverOrder_success() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    orderService.completeOrder(orderId);
    orderService.prepareOrder(orderId);
    orderService.deliverOrder(orderId);
    Set<UUID> preparedOrders = orderService.listPreparedOrders();

    assertThrows(IllegalArgumentException.class, () ->
        orderService.getOrderStatus(orderId), "Order should be removed after delivery");
    assertFalse(preparedOrders.contains(orderId), "Order should not be in prepared orders");
  }

  @Test
  public void testAddPancakeWithIngredients_success() {
    UUID orderId = orderService.createOrder(1, 101);
    List<UUID> ingredients = Arrays.asList(ingredient1Id, ingredient2Id);
    orderService.addPancake(orderId, ingredients);

    List<Pancake> pancakes = orderService.getPancakes(orderId);
    List<String> pancakeIngredients = pancakes.get(0).getIngredients();

    assertEquals(1, pancakes.size(), "Order should have one pancake");
    assertTrue(pancakeIngredients.contains("Dark chocolate"), "Pancake should contain Dark chocolate");
    assertTrue(pancakeIngredients.contains("Whipped cream"), "Pancake should contain Whipped cream");
  }

  @Test
  public void testAddPancake_invalidRecipe_throwsException() {
    UUID orderId = orderService.createOrder(1, 101);
    UUID invalidRecipeId = UUID.randomUUID();

    assertThrows(IllegalArgumentException.class, () -> orderService.addPancake(orderId, invalidRecipeId),
        "Should throw exception for invalid recipe ID");
  }

  @Test
  public void testAddPancake_orderCompleted_throwsException() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    orderService.completeOrder(orderId);

    assertThrows(IllegalArgumentException.class, () -> orderService.addPancake(orderId, recipeId),
        "Should throw exception when adding pancake to completed order");
  }

  @Test
  public void testRemovePancakes_success() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    List<Pancake> pancakes = orderService.getPancakes(orderId);
    Set<UUID> pancakeIds = pancakes.stream()
        .map(Pancake::getId)
        .collect(Collectors.toSet());

    orderService.removePancakes(orderId, pancakeIds);
    pancakes = orderService.getPancakes(orderId);

    assertTrue(pancakes.isEmpty(), "Order should have no pancakes after removal");
  }

  @Test
  public void testCancelOrder_success() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.cancelOrder(orderId);

    assertThrows(IllegalArgumentException.class, () -> orderService.getOrderStatus(orderId),
        "Order should be removed after cancellation");
  }

  @Test
  public void testCancelOrder_alreadyCompleted_throwsException() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    orderService.completeOrder(orderId);

    assertThrows(IllegalArgumentException.class, () ->
        orderService.cancelOrder(orderId), "Should throw exception when cancelling a completed order");
  }

  @Test
  public void testPrepareOrder_notCompleted_throwsException() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);

    assertThrows(IllegalArgumentException.class, () ->
        orderService.prepareOrder(orderId), "Should throw exception when preparing an order that is not completed");
  }

  @Test
  public void testDeliverOrder_notPrepared_throwsException() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    orderService.completeOrder(orderId);

    assertThrows(IllegalArgumentException.class, () ->
        orderService.deliverOrder(orderId), "Should throw exception when delivering an order that is not prepared");
  }

  @Test
  public void testViewOrder_success() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    List<String> orderDescriptions = orderService.viewOrder(orderId);

    assertEquals(1, orderDescriptions.size(), "Order should have one pancake description");
    assertTrue(orderDescriptions.get(0).contains("Dark chocolate"), "Description should contain Dark chocolate");
    assertTrue(orderDescriptions.get(0).contains("Whipped cream"), "Description should contain Whipped cream");
  }

  @Test
  public void testGetOrderStatus_invalidOrderId_throwsException() {
    UUID invalidOrderId = UUID.randomUUID();
    assertThrows(IllegalArgumentException.class, () ->
        orderService.getOrderStatus(invalidOrderId), "Should throw exception for invalid order ID");
  }

  @Test
  public void testListCompletedOrders_empty() {
    Set<UUID> completedOrders = orderService.listCompletedOrders();
    assertTrue(completedOrders.isEmpty(), "Completed orders should be empty initially");
  }

  @Test
  public void testListPreparedOrders_empty() {
    Set<UUID> preparedOrders = orderService.listPreparedOrders();
    assertTrue(preparedOrders.isEmpty(), "Prepared orders should be empty initially");
  }

  @Test
  public void testCompleteOrder_alreadyCompleted_throwsException() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    orderService.completeOrder(orderId);

    assertThrows(IllegalArgumentException.class, () ->
        orderService.completeOrder(orderId), "Should throw exception when completing an already completed order");
  }

  @Test
  public void testPrepareOrder_alreadyPrepared_throwsException() {
    UUID orderId = orderService.createOrder(1, 101);
    orderService.addPancake(orderId, recipeId);
    orderService.completeOrder(orderId);
    orderService.prepareOrder(orderId);

    assertThrows(IllegalArgumentException.class, () -> orderService.prepareOrder(orderId),
        "Should throw exception when preparing an already prepared order");
  }

  @Test
  public void testAddPancake_invalidIngredient_throwsException() {
    UUID orderId = orderService.createOrder(1, 101);
    UUID invalidIngredientId = UUID.randomUUID();
    List<UUID> ingredients = Arrays.asList(invalidIngredientId);

    assertThrows(IllegalArgumentException.class, () -> orderService.addPancake(orderId, ingredients),
        "Should throw exception for invalid ingredient ID");
  }

}
