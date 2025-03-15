import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Main class to run both Part 1 and Part 2
public class TemplateGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //new Part1GUI(); // Run Part 1 GUI
            new Part2GUI(); // Run Part 2 GUI
        });
    }
}

/**
 part 1 - JFrames and JPanels
 **/
class Part1GUI extends JFrame {
    public Part1GUI() {
        super("GUI Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // making the top panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setPreferredSize(new Dimension(500, 60));

        // adding the title
        JLabel titleLabel = new JLabel("Application Title");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        topPanel.add(titleLabel);

        // making the main section 1
        JPanel mainSection1 = new JPanel();
        mainSection1.setBackground(new Color(89, 237, 255));
        mainSection1.setPreferredSize(new Dimension(500, 280));

        // making the bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(142, 143, 255));
        bottomPanel.setPreferredSize(new Dimension(500, 60));

        // adding panels to the frame
        add(topPanel);
        add(mainSection1);
        add(bottomPanel);

        setVisible(true);
    }
}

/**
 part 2 - Staff Login GUI
 **/
class Part2GUI extends JFrame {
    public Part2GUI() {
        super("Template Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(getTopPanel());
        add(getMainContainer());
        add(getBottomPanel());

        setVisible(true);
    }

    /**
     * function to write the title
     * */
    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(142, 143, 255)); // colouring the background
        topPanel.setPreferredSize(new Dimension(500, 80));

        return topPanel;
    }

    /**
     * function to write the main section, split into four parts:
     *         1. Main Section 1
     *         2. Main Section 2
     *         3. Main Section 3
     *         4. Main Section 4
     * */
    private JPanel getMainContainer() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel section1 = getMainSection1();
        JPanel section2 = getMainSection2();
        JPanel section3 = getMainSection3();
        JPanel section4 = getMainSection4();

        mainContainer.add(section1);
        mainContainer.add(section2);
        mainContainer.add(section3);
        mainContainer.add(section4);

        return mainContainer;
    }


    /**
     * function to write section 1:
     * */
    private JPanel getMainSection1() {
        JPanel section1 = new JPanel();
        section1.setBackground(new Color(255, 137, 58)); // colouring the background
        section1.setPreferredSize(new Dimension(500, 30));

        return section1;
    }

    /**
     * function to write section 2
     * */
    private JPanel getMainSection2() {
        JPanel section2 = new JPanel();
        section2.setPreferredSize(new Dimension(500, 30));
        section2.setBackground(new Color(144, 255, 129)); // colouring the background

        return section2;
    }

    /**
     * function to write section 3
     * */
    private JPanel getMainSection3() {
        JPanel section3 = new JPanel();
        section3.setPreferredSize(new Dimension(500, 30));
        section3.setBackground(new Color(245, 210, 86)); // colouring the background

        return section3;
    }

    /**
     * function to write section 4
     * */
    private JPanel getMainSection4() {
        JPanel section4 = new JPanel();
        section4.setPreferredSize(new Dimension(500, 30));
        section4.setBackground(new Color(255, 166, 166)); // colouring the background

        return section4;
    }

    /**
     * Bottom panel has one part:
     *        1. Bottom Section
     * */
    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(255, 91, 255)); // colouring the background
        bottomPanel.setPreferredSize(new Dimension(500, 40));

        return bottomPanel;
    }
}
