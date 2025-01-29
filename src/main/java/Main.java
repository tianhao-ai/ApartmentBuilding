import models.*;
import gui.BuildingGUI;

public class Main {
    public static void main(String[] args) {
        // Create the building with initial requested temperature
        Building building = new Building(25.0);

        // Add initial apartments (Owner names are given, numbers assigned automatically)
        building.addRoom(new Apartment("Alice"));
        building.addRoom(new Apartment("Bob"));

        // Add common rooms
        building.addRoom(new CommonRoom(CommonRoom.RoomType.GYM));
        building.addRoom(new CommonRoom(CommonRoom.RoomType.LIBRARY));

        // Start periodic recalculation
        building.startRecalculationTask();

        // Launch GUI
        new BuildingGUI(building);
    }
}
