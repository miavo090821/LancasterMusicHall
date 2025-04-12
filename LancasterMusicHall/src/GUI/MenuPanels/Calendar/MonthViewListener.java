package GUI.MenuPanels.Calendar;

import java.time.LocalDate;

/**
 * The MonthViewListener interface provides a callback mechanism for handling day cell clicks
 * within a month view calendar.
 */
public interface MonthViewListener {
    /**
     * Invoked when a day cell within the month view is clicked.
     *
     * @param date the {@link LocalDate} representing the day that was clicked.
     */
    void onDayCellClicked(LocalDate date);
}
