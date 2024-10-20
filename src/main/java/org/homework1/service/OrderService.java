package org.homework1.service;

import org.homework1.constant.OrderStatus;
import org.homework1.model.Order;
import org.homework1.model.Pancake;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.homework1.constant.ValidationConstants.MAX_ORDER_SIZE;

public class OrderService {

  private final BuildingService buildingService = new BuildingService();
  private final RecipeService recipeService = new RecipeService();
  private final Map<UUID, Order> orderMap = new ConcurrentHashMap<>();
  private final Set<UUID> completedOrders = Collections.newSetFromMap(new ConcurrentHashMap<>());
  private final Set<UUID> preparedOrders = Collections.newSetFromMap(new ConcurrentHashMap<>());

  public UUID createOrder(int buildingNumber, int roomNumber) {
    buildingService.checkRoom(buildingNumber, roomNumber);
    final Order order = new Order(buildingNumber, roomNumber);
    orderMap.put(order.getId(), order);
    return order.getId(); // Return the orderId
  }

  public void cancelOrder(UUID orderId) {
    final Order canceledOrder = getOrder(orderId);
    synchronized (canceledOrder) {
      if (!canceledOrder.getStatus().equals(OrderStatus.DRAFT)) {
        throw new IllegalArgumentException("Order is already completed and cannot be canceled");
      }
      canceledOrder.setStatus(OrderStatus.CANCELED);
      orderMap.remove(orderId);
    }
  }

  public void completeOrder(UUID orderId) {
    final Order completedOrder = getOrder(orderId);
    synchronized (completedOrder) {
      if (!completedOrder.getStatus().equals(OrderStatus.DRAFT)) {
        throw new IllegalArgumentException("Order is already completed");
      }
      completedOrder.setStatus(OrderStatus.COMPLETED);
      completedOrders.add(orderId);
    }
  }

  public void prepareOrder(UUID orderId) {
    final Order preparedOrder = getOrder(orderId);
    synchronized (preparedOrder) {
      if (preparedOrder.getStatus().equals(OrderStatus.DRAFT)) {
        throw new IllegalArgumentException("Order needs to be completed before it can be prepared");
      }
      if (preparedOrder.getStatus().equals(OrderStatus.PREPARED)) {
        throw new IllegalArgumentException("Order is already prepared");
      }
      preparedOrder.setStatus(OrderStatus.PREPARED);
      preparedOrders.add(orderId);
      completedOrders.remove(orderId);
    }
  }

  public void deliverOrder(UUID orderId) {
    final Order deliveredOrder = getOrder(orderId);
    synchronized (deliveredOrder) {
      if (!deliveredOrder.getStatus().equals(OrderStatus.PREPARED)) {
        throw new IllegalArgumentException("Order needs to be prepared before it can be delivered");
      }
      orderMap.remove(orderId);
      preparedOrders.remove(orderId);
    }
  }

  public void addPancake(UUID orderId, UUID recipeId) {
    addPancake(orderId, recipeService.getRecipeIngredients(recipeId));
  }

  public void addPancake(UUID orderId, List<UUID> ingredients) {
    final Order order = getOrder(orderId);
    synchronized (order) {
      if (!order.getStatus().equals(OrderStatus.DRAFT)) {
        throw new IllegalArgumentException("Order is already completed and cannot be modified");
      }
      recipeService.validateRecipeIngredients(ingredients);
      final List<String> pancakeIngredients = ingredients.stream()
          .map(recipeService::getIngredientName)
          .collect(Collectors.toList());
      order.addPancake(new Pancake(pancakeIngredients));
      if (order.getPancakeCount() > MAX_ORDER_SIZE) {
        throw new IllegalArgumentException("Order cannot have more than " +  MAX_ORDER_SIZE + "pancakes");
      }
    }
  }

  public void removePancakes(UUID orderId, Set<UUID> pancakeIds) {
    final Order order = getOrder(orderId);
    synchronized (order) {
      if (!order.getStatus().equals(OrderStatus.DRAFT)) {
        throw new IllegalArgumentException("Order is already completed and cannot be modified");
      }
      order.removePancakes(pancakeIds);
    }
  }

  public List<String> viewOrder(UUID orderId) {
    final Order order = getOrder(orderId);
    return order.getPancakes().stream()
        .map(Pancake::description)
        .collect(Collectors.toList());
  }

  public OrderStatus getOrderStatus(UUID orderId) {
    return getOrder(orderId).getStatus();
  }

  public List<Pancake> getPancakes(UUID orderId) {
    return getOrder(orderId).getPancakes();
  }

  public Set<UUID> listCompletedOrders() {
    return new HashSet<>(completedOrders);
  }

  public Set<UUID> listPreparedOrders() {
    return new HashSet<>(preparedOrders);
  }

  private Order getOrder(UUID orderId) {
    return Optional.ofNullable(orderMap.get(orderId))
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
  }
}
