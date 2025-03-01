package operations.entities;

public class Seat {
    private char row;        // Row letter of the seat
    private int number;      // Seat number within the row
    private Type type;       // Type of seat (regular, restricted, wheelchair)
    private Status status;   // Seat status (available, sold, held)

    // Enum for seat types
    public enum Type {
        REGULAR, RESTRICTED, WHEELCHAIR, COMPANION
    }

    // Enum for seat status
    public enum Status {
        AVAILABLE, SOLD, HELD;
    }

    public String stringStatus(Status status){
        return switch (status) {
            case AVAILABLE -> "Available";
            case HELD -> "Held";
            case SOLD -> "Sold";
        };
    }
    public String stringType(Type type){
        return switch (type) {
            case COMPANION -> "Companion";
            case REGULAR -> "Regular";
            case RESTRICTED -> "Restricted";
            case WHEELCHAIR -> "Wheelchair";
            
        };
    }

    // Constructor
    public Seat(char row, int number, Type type, Status status) {
        this.row = row;
        this.number = number;
        this.status = status;
        this.type = type;

    }

    // Check if the seat is wheelchair accessible
    public boolean isWheelchairAccessible() {
        return this.type == Type.WHEELCHAIR;
    }

    // Check if the seat is designated for a companion
    public boolean isCompanionSeat() {
        return this.type == Type.COMPANION;
    }

    // Getters (optional, if needed)
    public char getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Seat:" +
                "row=" + row +
                ", number=" + number +
                ", type=" + stringType(type) +
                ", status=" + stringStatus(status) +
                "\n";
    }
}
