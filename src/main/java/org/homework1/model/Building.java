package org.homework1.model;

import org.homework1.util.IntegerRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Building(int buildingNumber, List<IntegerRange> rooms) {

  public Building {
        Objects.requireNonNull(rooms, "Rooms must not be null");
    }

  @Override
  public List<IntegerRange> rooms() {
    return new ArrayList<>(rooms);
  }

  public boolean hasRoom(int roomNumber) {
        return rooms.stream().anyMatch(range -> range.contains(roomNumber));
    }
}
