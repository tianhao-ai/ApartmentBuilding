import models.*;
import gui.BuildingGUI;

public class Main {
    public static void main(String[] args) {
        // Create the building with initial temperature
        Building building = new Building(25.0);

        // Add initial apartments
        building.addRoom(new Apartment("Owner 101"));
        building.addRoom(new Apartment("Owner 102"));

        // Add common rooms
        building.addRoom(new CommonRoom(CommonRoom.RoomType.GYM));
        building.addRoom(new CommonRoom(CommonRoom.RoomType.LIBRARY));

        // Start periodic recalculation
        building.startRecalculationTask();

        // Launch GUI
        new BuildingGUI(building);
    }
}
