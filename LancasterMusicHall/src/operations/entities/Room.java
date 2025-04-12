package operations.entities;

/**
 * The Room class represents a room within a venue with various capacity details
 * and seating arrangements.
 */
public class Room {
    private String roomName;
    private int venueId;
    private Integer roomNumber;
    private Integer roomCapacity;
    private int classroomCapacity;
    private int boardroomCapacity;
    private int presentationCapacity;
    private String seatingType;

    /**
     * Constructs a new Room with the specified details.
     *
     * @param roomName             the name of the room
     * @param venueId              the identifier of the venue where the room is located
     * @param roomNumber           the room number (can be null)
     * @param roomCapacity         the total capacity of the room (can be null)
     * @param classroomCapacity    the seating capacity in a classroom layout
     * @param boardroomCapacity    the seating capacity in a boardroom layout
     * @param presentationCapacity the seating capacity in a presentation layout
     * @param seatingType          the type of seating arrangement
     */
    public Room(String roomName, int venueId, Integer roomNumber, Integer roomCapacity,
                int classroomCapacity, int boardroomCapacity, int presentationCapacity, String seatingType) {
        this.roomName = roomName;
        this.venueId = venueId;
        this.roomNumber = roomNumber;
        this.roomCapacity = roomCapacity;
        this.classroomCapacity = classroomCapacity;
        this.boardroomCapacity = boardroomCapacity;
        this.presentationCapacity = presentationCapacity;
        this.seatingType = seatingType;
    }

    /**
     * Gets the name of the room.
     *
     * @return the room name
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets the identifier of the venue where this room is located.
     *
     * @return the venue ID
     */
    public int getVenueId() {
        return venueId;
    }

    /**
     * Gets the room number.
     *
     * @return the room number, or null if not set
     */
    public Integer getRoomNumber() {
        return roomNumber;
    }

    /**
     * Gets the total capacity of the room.
     *
     * @return the room capacity, or null if not set
     */
    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    /**
     * Gets the seating capacity when the room is arranged in a classroom layout.
     *
     * @return the classroom capacity
     */
    public int getClassroomCapacity() {
        return classroomCapacity;
    }

    /**
     * Gets the seating capacity when the room is arranged in a boardroom layout.
     *
     * @return the boardroom capacity
     */
    public int getBoardroomCapacity() {
        return boardroomCapacity;
    }

    /**
     * Gets the seating capacity when the room is arranged in a presentation layout.
     *
     * @return the presentation capacity
     */
    public int getPresentationCapacity() {
        return presentationCapacity;
    }

    /**
     * Gets the seating type of the room.
     *
     * @return the seating type
     */
    public String getSeatingType() {
        return seatingType;
    }

    /**
     * Returns a string representation of the Room object.
     *
     * @return a string containing the room's details
     */
    @Override
    public String toString() {
        return "Room{" +
                "roomName='" + roomName + '\'' +
                ", venueId=" + venueId +
                ", roomNumber=" + roomNumber +
                ", roomCapacity=" + roomCapacity +
                ", classroomCapacity=" + classroomCapacity +
                ", boardroomCapacity=" + boardroomCapacity +
                ", presentationCapacity=" + presentationCapacity +
                ", seatingType='" + seatingType + '\'' +
                '}';
    }
}
