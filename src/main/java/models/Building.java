package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Building {
    private List<Room> rooms;
    private double requestedTemperature;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
            room.updateTemperatureControl(requestedTemperature, threshold);
        }
    }

    public void startRecalculationTask() {
        scheduler.scheduleAtFixedRate(() -> {
            recalculateRooms();
            System.out.println("Scheduled Recalculation Performed:");
            for (Room room : rooms) {
                System.out.println(room);
            }
        }, 0, 10, TimeUnit.SECONDS); // Check every 10 seconds if heating/cooling should be enabled or disabled
    }

    public void stopRecalculationTask() {
        scheduler.shutdown();
        for (Room room : rooms) {
            room.stopTemperatureAdjustment();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Building { requestedTemperature=").append(requestedTemperature).append(" }\n");
        for (Room room : rooms) {
            sb.append(room).append("\n");
        }
        return sb.toString();
    }
}
