import models.*;

public class Main {
    public static void main(String[] args) {
        // Create the building
        Building building = new Building(25.0);

        // Add initial apartments
        building.addRoom(new Apartment("Owner 101"));
        building.addRoom(new Apartment("Owner 102"));

        // Add common rooms
        building.addRoom(new CommonRoom(CommonRoom.RoomType.GYM));
        building.addRoom(new CommonRoom(CommonRoom.RoomType.LIBRARY));

        // Initial state
        System.out.println("Initial State of Building:");
        System.out.println(building);

        // Simulate recalculation
        building.recalculateRooms();
        System.out.println("After Recalculation:");
        System.out.println(building);
    }
}
