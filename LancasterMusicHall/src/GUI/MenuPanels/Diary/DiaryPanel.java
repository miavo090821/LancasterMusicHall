package GUI.MenuPanels.Diary;

import GUI.MainMenuGUI;
import GUI.MenuPanels.Calendar.*;
import GUI.MenuPanels.Event.EventDetailForm;
import GUI.MenuPanels.Event.NewEventForm;
import Database.SQLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DiaryPanel extends JPanel {
    private enum DiaryView {DAY, WEEK, MONTH}
    private DiaryView currentView = DiaryView.DAY;
    private CalendarViewPanel currentViewPanel;
    private SQLConnection sqlCon;
    private MainMenuGUI mainMenu;
    private LocalDate currentDate = LocalDate.now();
    private List<operations.entities.Event> events = new ArrayList<>(); // Start empty

    // UI Components
    private JComboBox<String> viewCombo;

    private JLabel viewRangeLabel = new JLabel();

    // Date formatting
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    public DiaryPanel(MainMenuGUI mainMenu, SQLConnection sqlCon) {
        this.mainMenu = mainMenu;
        this.sqlCon = sqlCon;
        setLayout(new BorderLayout());
        switchToView(DiaryView.DAY);
        setupBottomPanel();
    }

    private void switchToView(DiaryView view) {
        currentView = view;

        if (currentViewPanel != null) {
            remove(currentViewPanel);
        }

        switch (view) {
            case DAY:
                currentViewPanel = new DiaryDayViewPanel(currentDate, events, sqlCon);
                break;
            case WEEK:
                currentViewPanel = new DiaryWeekViewPanel(currentDate, events, sqlCon);
                break;
            case MONTH:
                currentViewPanel = new DiaryMonthViewPanel(LocalDate.now(), events, sqlCon, new MonthViewListener() {
                    @Override
                    public void onDayCellClicked(LocalDate date) {
                        switchToView(DiaryView.WEEK, date);
                    }
                });
        }

        add(currentViewPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(Box.createHorizontalGlue());

        // View controls
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        viewPanel.setBackground(Color.WHITE);
        viewPanel.add(new JLabel("View:"));
        viewCombo = new JComboBox<>(new String[]{"Day", "Week", "Month"});
        viewCombo.setSelectedItem("Day");
        viewCombo.setPreferredSize(new Dimension(100, 25));
        viewCombo.addActionListener(e -> {
            String selected = (String) viewCombo.getSelectedItem();
            switch (selected) {
                case "Day": switchToView(DiaryView.DAY); break;
                case "Week": switchToView(DiaryView.WEEK); break;
                case "Month": switchToView(DiaryView.MONTH); break;
            }
        });
        viewPanel.add(viewCombo);
        bottomPanel.add(viewPanel);
        bottomPanel.add(Box.createHorizontalGlue());

        // New Draft button
        JButton newEventButton = new JButton("New Draft");
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 30));
        newEventButton.addActionListener(e -> {
            NewEventForm newEventForm = new NewEventForm(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    sqlCon
            );
            newEventForm.setVisible(true);
            newEventForm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    currentViewPanel.refreshView();
                }
            });
        });
        bottomPanel.add(newEventButton);
        bottomPanel.add(Box.createHorizontalGlue());

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        navPanel.setBackground(Color.WHITE);

        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> navigate(-1));

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> navigate(1));

        mainMenu.stylizeButton(prevButton);
        mainMenu.stylizeButton(nextButton);

        navPanel.add(prevButton);
        navPanel.add(nextButton);
        bottomPanel.add(navPanel);
        bottomPanel.add(Box.createHorizontalGlue());

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void navigate(int direction) {
        currentViewPanel.navigate(direction);
        updateView();
    }

    private void switchToView(DiaryView view, LocalDate date) {
        currentView = view;
        currentDate = date; // Update the current date

        if (currentViewPanel != null) {
            remove(currentViewPanel);
        }

        switch (view) {
            case DAY:
                currentViewPanel = new DiaryDayViewPanel(date, events, sqlCon);
                viewCombo.setSelectedItem("Day");
                break;
            case WEEK:
                currentViewPanel = new DiaryWeekViewPanel(date, events, sqlCon);
                viewCombo.setSelectedItem("Week");
                break;
            case MONTH:
                currentViewPanel = new DiaryMonthViewPanel(date, events, sqlCon, new MonthViewListener() {
                    @Override
                    public void onDayCellClicked(LocalDate clickedDate) {
                        switchToView(DiaryView.WEEK, clickedDate);
                    }
                });
                viewCombo.setSelectedItem("Month");
                break;
        }

        add(currentViewPanel, BorderLayout.CENTER);
        updateView();
    }

    private void updateView() {
        currentViewPanel.refreshView();
        updateHeaderText();
    }


    private void updateHeaderText() {
        String headerText = switch (currentView) {
            case WEEK -> currentViewPanel.getViewStartDate().format(dateFormatter) + " to " +
                    currentViewPanel.getViewEndDate().format(dateFormatter);
            case DAY -> currentViewPanel.getViewStartDate().format(dateFormatter);
            case MONTH -> currentViewPanel.getViewStartDate().format(monthYearFormatter);
        };
        viewRangeLabel.setText(headerText);
    }
}