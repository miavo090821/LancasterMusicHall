package GUI.MenuPanels;

import GUI.MainMenuGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class BookingPanel extends JPanel {
    public BookingPanel(MainMenuGUI mainMenu) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }
}
