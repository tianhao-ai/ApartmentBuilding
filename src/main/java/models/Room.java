package models;

import java.util.Random;

public class Room {
    private static int idCounter = 1;

    private final int id;
    private double currentTemperature;
    private boolean heatingEnabled;
    private boolean coolingEnabled;

    public Room() {
        this.id = idCounter++;
        this.currentTemperature = new Random().nextDouble() * (40 - 10) + 10; // Random temp 10°C to 40°C
        this.heatingEnabled = false;
        this.coolingEnabled = false;
    }

    public int getId() {
        return id;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public boolean isHeatingEnabled() {
        return heatingEnabled;
    }

    public void setHeatingEnabled(boolean heatingEnabled) {
        this.heatingEnabled = heatingEnabled;
    }

    public boolean isCoolingEnabled() {
        return coolingEnabled;
    }

    public void setCoolingEnabled(boolean coolingEnabled) {
        this.coolingEnabled = coolingEnabled;
    }

    public void updateTemperature(double requestedTemperature, double threshold) {
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

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", currentTemperature=" + currentTemperature +
                ", heatingEnabled=" + heatingEnabled +
                ", coolingEnabled=" + coolingEnabled +
                '}';
    }
}
