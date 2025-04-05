package GUI.MenuPanels.Calendar;

import java.time.LocalDate;

public interface MonthViewListener {
    void onDayCellClicked(LocalDate date);
}
