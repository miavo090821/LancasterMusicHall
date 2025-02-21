package operations.entities;

public class Seat {
    char row;          // Row number of the seat
    int number;       // Seat number within the row
    Type type;      // Type of seat
    Status status;      // Type of seat


    // Enum for seat types
    public enum Type {
        REGULAR, RESTRICTED, WHEELCHAIR
    }

    // Enum for seat status
    public enum Status {
        AVAILABLE, SOLD, HELD
    }

    // Constructor
    public Seat(char row, int number, Type type, Status status) {
        this.row = row;
        this.number = number;
        this.type = type;
        this.status = status;
    }
    @Override
    public String toString() {
        return "Seat{" +
                "row=" + row +
                ", number=" + number +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}'+'\n';
    }
}
