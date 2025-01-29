package models;

import java.util.ArrayList;
import java.util.List;

public class Building {
    private List<Room> rooms;
    private double requestedTemperature;

    public Building(double requestedTemperature) {
        this.rooms = new ArrayList<>();
        this.requestedTemperature = requestedTemperature;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public double getRequestedTemperature() {
        return requestedTemperature;
    }

    public void setRequestedTemperature(double requestedTemperature) {
        this.requestedTemperature = requestedTemperature;
        recalculateRooms();
    }

    public void recalculateRooms() {
        double threshold = 1.0; // "Close enough" threshold
        for (Room room : rooms) {
            room.updateTemperature(requestedTemperature, threshold);
        }
    }

    @Override
    public String toString() {
        return "Building{" +
                "rooms=" + rooms +
                ", requestedTemperature=" + requestedTemperature +
                '}';
    }
}
