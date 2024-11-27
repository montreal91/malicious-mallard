package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


class DummyUnitTest {

    @Test
    void testClickIncrementsClickCount() {
        // Arrange: Create a DummyUnit instance
        DummyUnit dummyUnit = new DummyUnit(1, null, "Test Dummy");

        // Act: Perform multiple clicks
        dummyUnit.click();
        dummyUnit.click();
        dummyUnit.click();

        // Assert: Verify the click count
        assertEquals(3, dummyUnit.getClicks(), "The click count should be 3 after three clicks.");
    }

    @Test
    void testInitialClickCountIsZero() {
        // Arrange: Create a DummyUnit instance
        DummyUnit dummyUnit = new DummyUnit(1, null, "Test Dummy");

        // Assert: Verify the initial click count is zero
        assertEquals(0, dummyUnit.getClicks(), "The initial click count should be zero.");
    }

    @Test
    void testGetIdReturnsCorrectValue() {
        // Arrange: Create a DummyUnit instance with a specific ID
        int expectedId = 42;
        DummyUnit dummyUnit = new DummyUnit(expectedId, null, "Test Dummy");

        // Assert: Verify the ID is correctly returned
        assertEquals(
                expectedId,
                dummyUnit.getId(),
                "The unit ID should match the one provided during construction."
        );
    }

    @Test
    void testGetNameReturnsCorrectValue() {
        // Arrange: Create a DummyUnit instance with a specific name
        String expectedName = "Awesome Dummy";
        DummyUnit dummyUnit = new DummyUnit(1, null, expectedName);

        // Assert: Verify the name is correctly returned
        assertEquals(
                expectedName,
                dummyUnit.getName(),
                "The unit name should match the one provided during construction."
        );
    }

    @Test
    void testGetOwnerIsEmptyForNeutralUnit() {
        // Arrange: Create a DummyUnit instance with a null owner
        DummyUnit dummyUnit = new DummyUnit(1, null, "Test Dummy");

        // Assert: Verify the owner is not present
        assertTrue(dummyUnit.getOwner().isEmpty(), "The owner should be empty for a neutral unit.");
    }

    @Test
    void testGetOwnerReturnsCorrectValue() {
        // Arrange: Create a Player instance and a DummyUnit with that player as the owner
        Player owner = new Player(1, "Player1", 1);
        DummyUnit dummyUnit = new DummyUnit(1, owner, "Test Dummy");

        // Assert: Verify the owner is correctly returned
        assertTrue(
                dummyUnit.getOwner().isPresent(),
                "The owner should be present for a unit with an assigned owner."
        );

        assertEquals(
                owner,
                dummyUnit.getOwner().get(),
                "The owner should match the one provided during construction."
        );
    }
}
