package models;

public class Apartment extends Room {
    private static int nextApartmentNumber = 101; // Start at 101

    private String ownerName;
    private final int apartmentNumber;

    public Apartment(String ownerName) {
        super();
        this.ownerName = ownerName;
        this.apartmentNumber = nextApartmentNumber++; // Assign and increment
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

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
