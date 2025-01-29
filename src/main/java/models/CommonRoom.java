package models;

/**
 * Represents a common area within the building.
 * Extends the basic Room functionality with specific types of common areas
 * such as gym, library, or laundry room.
 */
public class CommonRoom extends Room {
    /**
     * Enumeration of available common room types.
     * Each type represents a different facility available to building residents.
     */
    public enum RoomType {
        GYM, LIBRARY, LAUNDRY
    }

    private RoomType type;

    /**
     * Constructs a new CommonRoom with specified type.
     * @param type The type of common room (e.g., GYM, LIBRARY, LAUNDRY)
     */
    public CommonRoom(RoomType type) {
        super();
        this.type = type;
    }

    /**
     * @return The type of common room
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Updates the type of common room.
     * @param type The new room type to set
     */
    public void setType(RoomType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CommonRoom{" +
                "type=" + type +
                ", " + super.toString() +
                '}';
    }
}
