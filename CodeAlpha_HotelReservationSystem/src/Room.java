/**
 * A physical room of the hotel.
 */
public class Room {

    private final int roomNumber;
    private final RoomCategory category;
    private final int floor;

    public Room(int roomNumber, RoomCategory category, int floor) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.floor = floor;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public String toString() {
        return String.format("%-6d %-10s Floor %-3d %10.2f / night",
                roomNumber, category.getDisplayName(), floor, category.getPricePerNight());
    }
}
