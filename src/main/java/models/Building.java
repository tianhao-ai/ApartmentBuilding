package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Building {
    private List<Room> rooms;
    private double requestedTemperature;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

    public void startRecalculationTask() {
        scheduler.scheduleAtFixedRate(() -> {
            recalculateRooms();
            System.out.println("Scheduled Recalculation Performed:");
            System.out.println(this);
        }, 0, 10, TimeUnit.SECONDS); // Recalculate every 10 seconds
    }

    public void stopRecalculationTask() {
        scheduler.shutdown();
    }

    @Override
    public String toString() {
        return "Building{" +
                "rooms=" + rooms +
                ", requestedTemperature=" + requestedTemperature +
                '}';
    }
}
