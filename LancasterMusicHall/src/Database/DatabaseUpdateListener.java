package Database;

/**
 * Any class that wants to be notified about database updates
 * (e.g., booking changes, password resets, etc.) implements this interface.
 */
public interface DatabaseUpdateListener {
    /**
     * Called whenever a database update occurs.
     *
     * @param updateType A short string describing the type of update
     *                   (e.g., "bookingUpdate", "passwordReset", "reportUpdate").
     * @param data       Optional payload (e.g., an ID or object related to the update).
     */
    void databaseUpdated(String updateType, Object data);
}
