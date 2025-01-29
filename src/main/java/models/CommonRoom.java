package models;

public class CommonRoom extends Room {
    public enum RoomType {
        GYM, LIBRARY, LAUNDRY
    }

    private RoomType type;

    public CommonRoom(RoomType type) {
        super();
        this.type = type;
    }

    public RoomType getType() {
        return type;
    }

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
