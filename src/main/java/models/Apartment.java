package models;

import utils.PropertyLoader;

/**
 * Represents an apartment within the building.
 * Extends the basic Room functionality with apartment-specific features
 * such as owner information and apartment numbering.
 */
public class Apartment extends Room {
    /** Starting number for apartment numbering sequence */
    private static int nextApartmentNumber = PropertyLoader.getIntProperty("apartment.starting.number");

    private String ownerName;
    private final int apartmentNumber;

    /**
     * Constructs a new Apartment with specified owner.
     * Automatically assigns the next available apartment number.
     * @param ownerName The name of the apartment owner
     */
    public Apartment(String ownerName) {
        super();
        this.ownerName = ownerName;
        this.apartmentNumber = nextApartmentNumber++;
    }

    /**
     * @return The name of the apartment owner
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Updates the owner name for this apartment.
     * @param ownerName The new owner's name
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return The unique apartment number
     */
    public int getApartmentNumber() {
        return apartmentNumber;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "apartmentNumber=" + apartmentNumber +
                ", ownerName='" + ownerName + '\'' +
                ", " + super.toString() +
                '}';
    }
}
