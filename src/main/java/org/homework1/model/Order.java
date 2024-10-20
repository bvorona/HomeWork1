package org.homework1.model;

import org.homework1.constant.OrderStatus;

import java.util.*;

public class Order {
    private final UUID id;
    private final int building;
    private final int room;
    private OrderStatus status = OrderStatus.DRAFT;
    private final Map<UUID, Pancake> pancakes = new HashMap<>();

    public Order(int building, int room) {
        this.id = UUID.randomUUID();
        this.building = building;
        this.room = room;
    }

    public void addPancake(Pancake pancake) {
        Objects.requireNonNull(pancake, "Pancake cannot be null");
        pancakes.put(pancake.getId(), pancake);
    }

    public void removePancake(UUID pancakeId) {
        Objects.requireNonNull(pancakeId, "Pancake ID cannot be null");
        final Pancake removed = pancakes.remove(pancakeId);
        if (removed == null) {
            throw new IllegalArgumentException("Pancake not found");
        }
    }

    public void removePancakes(Set<UUID> pancakeIds) {
        Objects.requireNonNull(pancakeIds, "Pancake IDs cannot be null");
        pancakeIds.forEach(this::removePancake);
    }

    public int getPancakeCount() {
        return pancakes.size();
    }

    public List<Pancake> getPancakes() {
        return new ArrayList<>(pancakes.values());
    }

    public UUID getId() {
        return id;
    }

    public int getBuilding() {
        return building;
    }

    public int getRoom() {
        return room;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return building == order.building && room == order.room && Objects.equals(id, order.id) && status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, building, room, status);
    }
}
