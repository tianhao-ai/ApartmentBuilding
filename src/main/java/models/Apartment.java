package models;

public class Apartment extends Room {
    private String ownerName;

    public Apartment(String ownerName) {
        super();
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "ownerName='" + ownerName + '\'' +
                ", " + super.toString() +
                '}';
    }
}
