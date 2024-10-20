package org.homework1.service;

import org.homework1.constant.ValidationConstants;
import org.homework1.model.Building;
import org.homework1.util.IntegerRange;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BuildingService {

  private final Map<Integer, Building> buildings = new HashMap<>();
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final Lock readLock = lock.readLock();
  private final Lock writeLock = lock.writeLock();

  public void addBuilding(int buildingNumber, IntegerRange... roomRanges) {
    writeLock.lock();
    try {
      addBuildingInternal(buildingNumber, roomRanges);
    } finally {
      writeLock.unlock();
    }
  }

  private void addBuildingInternal(int buildingNumber, IntegerRange... roomRanges) {
    validateBuildingNumber(buildingNumber);
    validateRoomRanges(roomRanges);
    buildings.put(buildingNumber, new Building(buildingNumber, Arrays.asList(roomRanges)));
  }

  public void removeBuilding(int buildingNumber) {
    writeLock.lock();
    try {
      removeBuildingInternal(buildingNumber);
    } finally {
      writeLock.unlock();
    }
  }

  private void removeBuildingInternal(int buildingNumber) {
    final Building removed = buildings.remove(buildingNumber);
    if (removed == null) {
      throw new IllegalArgumentException("Building not found");
    }
  }

  public Building getBuilding(int buildingNumber) {
    readLock.lock();
    try {
      return buildings.get(buildingNumber);
    } finally {
      readLock.unlock();
    }
  }

  public void checkRoom(int buildingNumber, int roomNumber) {
    readLock.lock();
    Building building;
    try {
      building = buildings.get(buildingNumber);
    } finally {
      readLock.unlock();
    }
    if (building == null) {
      throw new IllegalArgumentException("Building not found");
    }
    if (!building.hasRoom(roomNumber)) {
      throw new IllegalArgumentException("Room not found");
    }
  }

  private void validateBuildingNumber(int buildingNumber) {
    if (buildingNumber <= 0) {
      throw new IllegalArgumentException("Building number must be a positive integer");
    }
    if (buildingNumber > ValidationConstants.MAX_BUILDING_NUMBER) {
      throw new IllegalArgumentException("Building number must be less than or equal to " + ValidationConstants.MAX_BUILDING_NUMBER);
    }
    if (buildings.containsKey(buildingNumber)) {
      throw new IllegalArgumentException("Building with the same number already exists");
    }
  }

  private void validateRoomRanges(IntegerRange... roomRanges) {
    if (roomRanges == null || roomRanges.length == 0) {
      throw new IllegalArgumentException("Building must have at least one room");
    }
    if (Arrays.stream(roomRanges).anyMatch(range -> range.start() > range.end())) {
      throw new IllegalArgumentException("Room range start must be less than or equal to end");
    }
    if (Arrays.stream(roomRanges).anyMatch(range -> range.start() <= 0)) {
      throw new IllegalArgumentException("Room numbers must be positive integers");
    }
    if (Arrays.stream(roomRanges).anyMatch(range -> range.end() > ValidationConstants.MAX_ROOM_NUMBER)) {
      throw new IllegalArgumentException("Room number must be less than or equal to " + ValidationConstants.MAX_ROOM_NUMBER);
    }
  }
}
