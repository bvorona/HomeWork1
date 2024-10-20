package org.homework1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.homework1.util.IntegerRange;

import static org.junit.jupiter.api.Assertions.*;
import static org.homework1.constant.ValidationConstants.MAX_BUILDING_NUMBER;
import static org.homework1.constant.ValidationConstants.MAX_ROOM_NUMBER;

public class BuildingServiceTest {

  private BuildingService buildingService;

  @BeforeEach
  public void setUp() {
    buildingService = new BuildingService();
  }

  @Test
  public void testAddBuilding_success() {
    buildingService.addBuilding(1, new IntegerRange(101, 199));
    assertNotNull(buildingService.getBuilding(1), "Building should be added successfully");
  }

  @Test
  public void testAddBuilding_duplicateBuildingNumber_throwsException() {
    buildingService.addBuilding(1, new IntegerRange(101, 199));
    assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(1, new IntegerRange(201, 299)));
  }

  @Test
  public void testAddBuilding_invalidBuildingNumberZero_throwsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(0, new IntegerRange(101, 199)));
  }

  @Test
  public void testAddBuilding_invalidBuildingNumberNegative_throwsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(-1, new IntegerRange(101, 199)));
  }

  @Test
  public void testAddBuilding_buildingNumberExceedsMax_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(MAX_BUILDING_NUMBER + 1, new IntegerRange(101, 199)));
  }

  @Test
  public void testAddBuilding_nullRoomRanges_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(1, (IntegerRange[]) null));
  }

  @Test
  public void testAddBuilding_emptyRoomRanges_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(1));
  }

  @Test
  public void testAddBuilding_invalidRoomRangeStartGreaterThanEnd_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(1, new IntegerRange(199, 101)));
  }

  @Test
  public void testAddBuilding_invalidRoomNumberZero_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(1, new IntegerRange(0, 10)));
  }

  @Test
  public void testAddBuilding_invalidRoomNumberNegative_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(1, new IntegerRange(-5, 10)));
  }

  @Test
  public void testAddBuilding_roomNumberExceedsMax_throwsException() {
    assertThrows(IllegalArgumentException.class, () ->
        buildingService.addBuilding(1, new IntegerRange(MAX_ROOM_NUMBER - 5, MAX_ROOM_NUMBER + 1)));
  }

  @Test
  public void testAddBuilding_multipleRoomRanges_success() {
    buildingService.addBuilding(1, new IntegerRange(101, 199), new IntegerRange(201, 299));

    assertNotNull(buildingService.getBuilding(1), "Building with multiple room ranges should be added");
  }

  @Test
  public void testRemoveBuilding_success() {
    buildingService.addBuilding(1, new IntegerRange(101, 199));
    buildingService.removeBuilding(1);

    assertNull(buildingService.getBuilding(1), "Building should be removed successfully");
  }

  @Test
  public void testRemoveBuilding_nonExistingBuilding_throwsException() {
    buildingService.addBuilding(1, new IntegerRange(101, 199));

    assertThrows(IllegalArgumentException.class, () -> buildingService.removeBuilding(2));
  }

  @Test
  public void testCheckRoom_validBuildingAndRoom_passes() {
    buildingService.addBuilding(1, new IntegerRange(101, 199), new IntegerRange(201, 299));

    assertDoesNotThrow(() -> buildingService.checkRoom(1, 150), "Should pass for valid building and room");
    assertDoesNotThrow(() -> buildingService.checkRoom(1, 250), "Should pass for valid building and room");
  }

  @Test
  public void testCheckRoom_nonExistingBuilding_throwsException() {
    buildingService.addBuilding(1, new IntegerRange(101, 199));

    assertThrows(IllegalArgumentException.class, () -> buildingService.checkRoom(2, 150));
  }

  @Test
  public void testCheckRoom_roomNotInAnyRange_throwsException() {
    buildingService.addBuilding(1, new IntegerRange(101, 199), new IntegerRange(201, 241));

    assertThrows(IllegalArgumentException.class, () -> buildingService.checkRoom(1, 250));
  }

  @Test
  public void testAddBuilding_roomRangeWithZeroLength_success() {
    buildingService.addBuilding(1, new IntegerRange(101, 101));

    assertNotNull(buildingService.getBuilding(1), "Building with zero-length room range should be added");
    assertDoesNotThrow(() -> buildingService.checkRoom(1, 101), "Should find room 101 in building");
  }
}
