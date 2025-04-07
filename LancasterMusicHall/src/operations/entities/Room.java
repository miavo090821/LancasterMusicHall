package operations.entities;

public class Room {
    private String roomName;
    private int venueId;
    private Integer roomNumber;
    private Integer roomCapacity;
    private int classroomCapacity;
    private int boardroomCapacity;
    private int presentationCapacity;
    private String seatingType;

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

    public String getRoomName() {
        return roomName;
    }

    public int getVenueId() {
        return venueId;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    public int getClassroomCapacity() {
        return classroomCapacity;
    }

    public int getBoardroomCapacity() {
        return boardroomCapacity;
    }

    public int getPresentationCapacity() {
        return presentationCapacity;
    }

    public String getSeatingType() {
        return seatingType;
    }

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
