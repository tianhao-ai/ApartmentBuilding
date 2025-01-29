package models;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import utils.PropertyLoader;

/**
 * Represents a room in the building with temperature control capabilities.
 * Each room has its own temperature management system that can operate independently,
 * with the ability to heat or cool the space to reach a target temperature.
 */
public class Room {
    private static int idCounter = 1;

    private final int id;
    private double currentTemperature;
    private boolean heatingEnabled;
    private boolean coolingEnabled;

    /** Rate at which temperature changes when heating/cooling is active */
    private static final double TEMP_CHANGE_RATE = PropertyLoader.getDoubleProperty("temperature.change.rate");
    
    /** Interval (in seconds) between temperature updates */
    private static final double TEMP_UPDATE_INTERVAL = PropertyLoader.getDoubleProperty("temperature.update.interval");

    /** Scheduler for managing temperature adjustment tasks */
    private final ScheduledExecutorService temperatureScheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructs a new Room with a randomly initialized temperature.
     * Starts the temperature adjustment monitoring system.
     */
    public Room() {
        this.id = idCounter++;
        // Initialize with random temperature between 10°C and 40°C
        this.currentTemperature = new Random().nextDouble() * (40 - 10) + 10;
        this.heatingEnabled = false;
        this.coolingEnabled = false;

        startTemperatureAdjustment();
    }

    /**
     * @return The unique identifier of the room
     */
    public int getId() {
        return id;
    }

    /**
     * @return The current temperature of the room in Celsius
     */
    public double getCurrentTemperature() {
        return currentTemperature;
    }

    /**
     * @return true if heating is currently active, false otherwise
     */
    public boolean isHeatingEnabled() {
        return heatingEnabled;
    }

    /**
     * @return true if cooling is currently active, false otherwise
     */
    public boolean isCoolingEnabled() {
        return coolingEnabled;
    }

    /**
     * Enables or disables the heating system.
     * @param enable true to enable heating, false to disable
     */
    public void enableHeating(boolean enable) {
        this.heatingEnabled = enable;
    }

    /**
     * Enables or disables the cooling system.
     * @param enable true to enable cooling, false to disable
     */
    public void enableCooling(boolean enable) {
        this.coolingEnabled = enable;
    }

    /**
     * Initiates periodic temperature adjustment based on heating/cooling status.
     * Runs on a fixed schedule defined by TEMP_UPDATE_INTERVAL.
     */
    private void startTemperatureAdjustment() {
        temperatureScheduler.scheduleAtFixedRate(() -> {
            if (heatingEnabled) {
                currentTemperature += TEMP_CHANGE_RATE;
            } else if (coolingEnabled) {
                currentTemperature -= TEMP_CHANGE_RATE;
            }
        }, 0, (long) TEMP_UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * Updates the temperature control systems based on the requested temperature and threshold.
     * @param requestedTemperature The target temperature to maintain
     * @param threshold The acceptable temperature difference threshold
     */
    public void updateTemperatureControl(double requestedTemperature, double threshold) {
        if (Math.abs(currentTemperature - requestedTemperature) <= threshold) {
            heatingEnabled = false;
            coolingEnabled = false;
        } else if (currentTemperature < requestedTemperature) {
            heatingEnabled = true;
            coolingEnabled = false;
        } else {
            heatingEnabled = false;
            coolingEnabled = true;
        }
    }

    /**
     * Stops the temperature adjustment scheduler.
     * Should be called when the room is no longer in use.
     */
    public void stopTemperatureAdjustment() {
        temperatureScheduler.shutdown();
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", currentTemperature=" + String.format("%.2f", currentTemperature) +
                ", heatingEnabled=" + heatingEnabled +
                ", coolingEnabled=" + coolingEnabled +
                '}';
    }
}
