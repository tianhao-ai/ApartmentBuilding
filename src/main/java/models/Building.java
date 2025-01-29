package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import utils.PropertyLoader;

/**
 * Represents a building with multiple rooms and centralized temperature control.
 * Manages temperature regulation across all rooms and provides periodic monitoring
 * and adjustment of room temperatures to maintain desired settings.
 */
public class Building {
    private List<Room> rooms;
    private double requestedTemperature;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructs a new Building with specified target temperature.
     * @param requestedTemperature The initial target temperature for all rooms
     */
    public Building(double requestedTemperature) {
        this.rooms = new ArrayList<>();
        this.requestedTemperature = requestedTemperature;
    }

    /**
     * Adds a new room to the building.
     * @param room The room to be added
     */
    public void addRoom(Room room) {
        rooms.add(room);
    }

    /**
     * @return List of all rooms in the building
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @return The current target temperature for the building
     */
    public double getRequestedTemperature() {
        return requestedTemperature;
    }

    /**
     * Updates the target temperature for the entire building and
     * triggers immediate recalculation of all room temperatures.
     * @param requestedTemperature The new target temperature
     */
    public void setRequestedTemperature(double requestedTemperature) {
        this.requestedTemperature = requestedTemperature;
        recalculateRooms();
    }

    /**
     * Recalculates and adjusts temperature controls for all rooms
     * based on the current target temperature and threshold settings.
     */
    public void recalculateRooms() {
        double threshold = PropertyLoader.getDoubleProperty("temperature.threshold");
        for (Room room : rooms) {
            room.updateTemperatureControl(requestedTemperature, threshold);
        }
    }

    /**
     * Initiates periodic temperature recalculation task.
     * Runs every 10 seconds to ensure all rooms maintain target temperature.
     */
    public void startRecalculationTask() {
        scheduler.scheduleAtFixedRate(() -> {
            recalculateRooms();
            System.out.println("Scheduled Recalculation Performed:");
            for (Room room : rooms) {
                System.out.println(room);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Stops all temperature control tasks in the building.
     * Shuts down the scheduler and stops temperature adjustment in all rooms.
     */
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
