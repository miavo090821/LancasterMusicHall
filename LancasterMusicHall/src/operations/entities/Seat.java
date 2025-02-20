package operations.entities;

public class Seat {
    public enum Type {
        REGULAR, VIP, WHEELCHAIR, RESTRICTED_VIEW
    }

    private char row;
    private int number;
    private Type type;
    private boolean isAvailable;

    public Seat(char row, int number, Type type) {
        this.row = row;
        this.number = number;
        this.type = type;
        this.isAvailable = true;
    }

    public void bookSeat() { this.isAvailable = false; }
    public void releaseSeat() { this.isAvailable = true; }

    // Getters and Setters
}
