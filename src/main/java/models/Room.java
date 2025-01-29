package models;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Room {
    private static int idCounter = 1;

    private final int id;
    private double currentTemperature;
    private boolean heatingEnabled;
    private boolean coolingEnabled;

    private static final double TEMP_CHANGE_RATE = 0.1; // Change per second
    private static final double TEMP_UPDATE_INTERVAL = 1; // Seconds

    private final ScheduledExecutorService temperatureScheduler = Executors.newScheduledThreadPool(1);

    public Room() {
        this.id = idCounter++;
        this.currentTemperature = new Random().nextDouble() * (40 - 10) + 10; // Random temp 10°C to 40°C
        this.heatingEnabled = false;
        this.coolingEnabled = false;

        // Start independent temperature adjustment
        startTemperatureAdjustment();
    }

    public int getId() {
        return id;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public boolean isHeatingEnabled() {
        return heatingEnabled;
    }

    public boolean isCoolingEnabled() {
        return coolingEnabled;
    }

    public void enableHeating(boolean enable) {
        this.heatingEnabled = enable;
    }

    public void enableCooling(boolean enable) {
        this.coolingEnabled = enable;
    }

    private void startTemperatureAdjustment() {
        temperatureScheduler.scheduleAtFixedRate(() -> {
            if (heatingEnabled) {
                currentTemperature += TEMP_CHANGE_RATE; // Increase temperature
            } else if (coolingEnabled) {
                currentTemperature -= TEMP_CHANGE_RATE; // Decrease temperature
            }
        }, 0, (long) TEMP_UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    public void updateTemperatureControl(double requestedTemperature, double threshold) {
        if (Math.abs(currentTemperature - requestedTemperature) <= threshold) {
            // Stop heating/cooling if the temperature is within the threshold
            heatingEnabled = false;
            coolingEnabled = false;
        } else if (currentTemperature < requestedTemperature) {
            // Enable heating
            heatingEnabled = true;
            coolingEnabled = false;
        } else {
            // Enable cooling
            heatingEnabled = false;
            coolingEnabled = true;
        }
    }

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
